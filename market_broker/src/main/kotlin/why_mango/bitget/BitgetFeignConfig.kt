package why_mango.bitget

import com.google.gson.Gson
import feign.*
import feign.codec.*
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.dto.BaseDto
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.upbit.dto.UpbitErrorResponse
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

@Configuration
class BitgetFeignConfig {
    private val gson: Gson = Gson()
    private val baseUrl = "https://api.bitget.com"
//    private val errorMap = emptyMap()

    @Bean
    fun bitgetRest(): BitgetRest = CoroutineFeign.builder<Void>()
        .decoder(BitgetGsonDecoder(gson))
        .encoder(encoder())
        .logger(Slf4jLogger())
        .errorDecoder(errorDecoder())
        .logLevel(Logger.Level.FULL)
        .target(BitgetRest::class.java, baseUrl)

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
        if (o is BaseDto) {
            t.body(format.encodeToString(o))
        } else {
            throw MangoShakeException(ErrorCode.UPBIT_ERROR, "BaseDto를 상속받은 클래스만 사용 가능합니다.")
        }
    }

    fun errorDecoder(): ErrorDecoder = ErrorDecoder { _: String, r: Response ->
        val response = r.body().asReader(StandardCharsets.UTF_8).use { it.readText() }

        val (upbitError) = try {
            @OptIn(ExperimentalSerializationApi::class)
            val format = Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
                isLenient = true
                explicitNulls = false
            }
            format.decodeFromString<UpbitErrorResponse>(response)
        } catch (e: Exception) {
            throw MangoShakeException(ErrorCode.UPBIT_ERROR, response)
        }

        throw MangoShakeException(ErrorCode.UPBIT_ERROR, "[${upbitError.name}] ${upbitError.message ?: ""}")
//        throw MangoShakeException(
//            errorCode = errorMap[upbitError.name] ?: ErrorCode.UPBIT_ERROR, "[${upbitError.name}] ${upbitError.message ?: ""}",
//            data = mapOf("requestUrl" to r.request().url())
//        )
    }
}