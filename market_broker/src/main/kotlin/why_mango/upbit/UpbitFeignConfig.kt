package why_mango.upbit

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import feign.*
import feign.codec.*
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.serializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.dto.FeignBaseBody
import java.lang.reflect.Type
import java.util.*

@Configuration
class UpbitFeignConfig(
    private val properties: UpbitProperties
) {

    @Bean
    fun upbitRest(): UpbitRest = CoroutineFeign.builder<Void>()
        .decoder(upbitDecoder())
        .encoder(upbitEncoder())
        .logger(Slf4jLogger())
        .logLevel(Logger.Level.FULL)
        .requestInterceptor(upbitRequestInterceptor())
        .target(UpbitRest::class.java, properties.url)

    fun upbitRequestInterceptor(): RequestInterceptor = RequestInterceptor {
        val algorithm: Algorithm = Algorithm.HMAC256(properties.secretKey)
        val jwtToken: String = JWT.create()
            .withClaim("access_key", properties.accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)

        it.header("Authorization", "Bearer $jwtToken")
    }

    fun upbitDecoder(): Decoder = Decoder { r: Response, t: Type ->
        // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
        @OptIn(ExperimentalSerializationApi::class)
        val format = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            isLenient = true
        }
        val serializer = format.serializersModule.serializer(t)
        val body = r.body().asReader(r.charset()).use { it.readText() }
        format.decodeFromString(serializer, body)
    }

    fun upbitEncoder(): Encoder = Encoder { o: Any, _: Type, t: RequestTemplate ->
        @OptIn(ExperimentalSerializationApi::class)
        val format = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            decodeEnumsCaseInsensitive = true
            explicitNulls = false
        }
        if (o is FeignBaseBody) {
            t.body(format.encodeToString(o))
        } else {
            throw RuntimeException("Unknown type")
        }
    }

    @ConfigurationProperties(prefix = "upbit")
    data class UpbitProperties (
        val accessKey: String,
        val secretKey: String,
        val url: String
    )
}