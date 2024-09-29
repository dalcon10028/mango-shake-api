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
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.upbit.dto.UpbitErrorResponse
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.util.*

@Configuration
class UpbitFeignConfig(
    private val properties: UpbitProperties
) {
    private val errorMap = mapOf(
        "no_authorization_token" to ErrorCode.OPEN_API_AUTH_ERROR,
    )

    @Bean
    fun upbitRest(): UpbitRest = CoroutineFeign.builder<Void>()
        .decoder(decoder())
        .encoder(encoder())
        .logger(Slf4jLogger())
        .errorDecoder(errorDecoder())
        .logLevel(Logger.Level.FULL)
        .requestInterceptor(RequestInterceptor())
        .target(UpbitRest::class.java, properties.url)

    private fun RequestInterceptor(): RequestInterceptor = RequestInterceptor {
        val algorithm: Algorithm = Algorithm.HMAC256(properties.secretKey)
        val jwtToken: String = JWT.create()
            .withClaim("access_key", properties.accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)

        it.header("Authorization", "Bearer $jwtToken")
    }

    private fun decoder(): Decoder = Decoder { r: Response, t: Type ->
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

    private fun encoder(): Encoder = Encoder { o: Any, _: Type, t: RequestTemplate ->
        @OptIn(ExperimentalSerializationApi::class)
        val format = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            decodeEnumsCaseInsensitive = true
            explicitNulls = false
        }
        if (o is FeignBaseBody) {
            t.body(format.encodeToString(o))
        } else {
            throw MangoShakeException(ErrorCode.UPBIT_ERROR, "FeignBaseBody를 상속받은 클래스만 사용 가능합니다.")
        }
    }

    fun errorDecoder(): ErrorDecoder = ErrorDecoder { _: String, r: Response ->
        val response = r.body().asReader(StandardCharsets.UTF_8).use { it.readText() }

        val (upbitError) = try {
            @OptIn(ExperimentalSerializationApi::class)
            val format = Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                isLenient = true
            }
            format.decodeFromString(UpbitErrorResponse.serializer(), response)
        } catch (e: Exception) {
            throw MangoShakeException(ErrorCode.UPBIT_ERROR, response)
        }

        throw MangoShakeException(errorMap[upbitError.name] ?: ErrorCode.UPBIT_ERROR, "[${upbitError.name}] ${upbitError.message}")
    }

    @ConfigurationProperties(prefix = "upbit")
    data class UpbitProperties(
        val accessKey: String,
        val secretKey: String,
        val url: String
    )
}