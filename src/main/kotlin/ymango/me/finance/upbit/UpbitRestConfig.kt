package ymango.me.finance.upbit

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

@Configuration
class UpbitRestConfig(
    private val upbitRestProperties: UpbitRestProperties
) {

    @Bean
    fun upbitRestApi(): UpbitRestApi = WebClient.builder()
        .baseUrl(upbitRestProperties.url)
        .defaultRequest {
            it.httpRequest { request ->
                request.headers.apply {
                    setBearerAuth(generateToken(request.uri.query))
                }
            }
        }
        .build()
        .let { WebClientAdapter.create(it) }
        .let { HttpServiceProxyFactory.builderFor(it).build() }
        .createClient(UpbitRestApi::class.java)

    private fun generateToken(query: String?): String {
        val algorithm: Algorithm = Algorithm.HMAC256(upbitRestProperties.secretKey)
        val token = JWT.create()
            .withClaim("access_key", upbitRestProperties.accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())

        if (!query.isNullOrBlank()) {
            val md = MessageDigest.getInstance("SHA-512").apply { update(query.toByteArray()) }
            val queryHash = java.lang.String.format("%0128x", BigInteger(1, md.digest()))
            token.withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
        }

        return token.sign(algorithm)
    }


    @ConfigurationProperties(prefix = "finance.upbit.rest")
    data class UpbitRestProperties (
        val url: String,
        val accessKey: String,
        val secretKey: String
    )
}