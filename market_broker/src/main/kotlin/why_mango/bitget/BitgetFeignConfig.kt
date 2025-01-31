package why_mango.bitget

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import feign.*
import feign.codec.*
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.kotlin.CoroutineFeign
import feign.slf4j.Slf4jLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.bitget.config.BitgetProperties
import why_mango.bitget.dto.BitgetResponse
import why_mango.bitget.exception.BitgetException
import why_mango.serialization.gson.NumberStringSerializer
import java.math.BigDecimal

import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Configuration
class BitgetFeignConfig(
    private val bitgetProperties: BitgetProperties,
) {
    private val gson: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(BigDecimal::class.java, NumberStringSerializer)
        .create()
    private val mac: Mac = Mac.getInstance("HmacSHA256")

    @Bean
    fun bitgetRest(): BitgetRest = CoroutineFeign.builder<Void>()
        .decoder(GsonDecoder(gson))
        .encoder(GsonEncoder(gson))
        .requestInterceptors(listOf(
            requestInterceptor(),
        ))
        .logger(Slf4jLogger())
        .errorDecoder(errorDecoder())
        .logLevel(Logger.Level.FULL)
        .target(BitgetRest::class.java, bitgetProperties.baseUrl)

    private fun requestInterceptor(): RequestInterceptor = RequestInterceptor { template ->
        val timestamp = System.currentTimeMillis().toString()
        template.header("Content-Type", "application/json")
        template.header("Accept", "application/json")
        template.header("locale", "ko-KR")
        template.header("ACCESS-KEY", bitgetProperties.accessKey)
        template.header("ACCESS-PASSPHRASE", bitgetProperties.passphrase)
        template.header("ACCESS-TIMESTAMP", timestamp)
        template.header("ACCESS-SIGN", generateSignature(timestamp, template.method(), template.path(), template.queryLine(), template.body()))
    }

    fun errorDecoder(): ErrorDecoder = ErrorDecoder { _: String, r: Response ->
        val response = r.body().asReader(StandardCharsets.UTF_8).use { it.readText() }

        val bitgetError: BitgetResponse<*> = try {
            gson.fromJson(response, BitgetResponse::class.java)
        } catch (e: Exception) {
            throw BitgetException(response)
        }

        throw BitgetException(bitgetError)
    }

    private fun generateSignature(timestamp: String, method: String, requestPath: String, queryString: String, body: ByteArray?): String {
        val serializedBody = body?.toString(charset("UTF-8")) ?: ""
        val preHash = "$timestamp$method$requestPath$queryString$serializedBody"
        val secretKeyBytes = bitgetProperties.secretKey.toByteArray(charset("UTF-8"))
        val secretKeySpec = SecretKeySpec(secretKeyBytes, "HmacSHA256")
        mac.init(secretKeySpec)
        return Base64.getEncoder().encodeToString(mac.doFinal(preHash.toByteArray(charset("UTF-8"))))
    }
}