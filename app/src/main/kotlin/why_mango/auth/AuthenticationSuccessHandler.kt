package why_mango.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.user.*
import why_mango.user.enums.AuthProvider
import java.net.URI

@Component
class AuthenticationSuccessHandler(
    private val userService: UserService,
    private val tokenProvider: JwtTokenProvider,
    @Value("\${app.web-base-url}") private val webBaseUrl: String
): ServerAuthenticationSuccessHandler {
    private val logger = KotlinLogging.logger {}
    private val redirectStrategy: ServerRedirectStrategy = DefaultServerRedirectStrategy()

    override fun onAuthenticationSuccess(webFilterExchange: WebFilterExchange, authentication: Authentication): Mono<Void> = mono {
        val auth = authentication as OAuth2AuthenticationToken
        // 회원가입 여부
        var isSignUp: Boolean = false
        val user: UserModel = userService.findByUsername(auth.principal.name) ?: run {
            val userCreate = when (auth.authorizedClientRegistrationId) {
                /**
                 *  id, connected_at, properties, kakao_account
                 *  properties : nickname, profile_image, thumbnail_image
                 */
                "kakao" -> UserCreate(
                    provider = AuthProvider.KAKAO,
                    username = auth.principal.name,
                    nickname = (auth.principal.attributes["properties"] as Map<*, *>)["nickname"] as String,
                    profileImageUrl = (auth.principal.attributes["properties"] as Map<*, *>)["profile_image"] as String,
                )
                else -> { throw MangoShakeException(ErrorCode.ILLEGAL_STATE, "Not supported provider") }
            }
            isSignUp = true
            userService.create(userCreate)
        }

        webFilterExchange.exchange.response.also {
            it.addCookie(
                ResponseCookie.from("accessToken", user.toAccessToken())
                    .path("/")
                    .maxAge(60) // 1분
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Lax")
                    .domain("localhost")
                    .build()
            )
        }

        redirectStrategy.sendRedirect(
            webFilterExchange.exchange,
            // 회원가입시 승인 대기 페이지로 이동
            URI.create(webBaseUrl + if (isSignUp) "/approval" else "")
        ).awaitSingleOrNull()

        null
    }

    private suspend fun UserModel.toAccessToken(): String {
        return tokenProvider.generateAccessToken(this)
    }
}