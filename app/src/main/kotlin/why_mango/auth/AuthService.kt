package why_mango.auth

import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import why_mango.auth.model.AuthUser
import why_mango.user.UserService

@Service
class AuthService(
    private val userService: UserService,
): ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User>, ReactiveUserDetailsService{
    private val delegateOAuth2UserService = DefaultReactiveOAuth2UserService()

    // https://velog.io/@van1164/kopring-spring-security-1
    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        return delegateOAuth2UserService.loadUser(userRequest).map { user -> AuthUser.from(user) }
    }

    // TODO: Implement this method for email login
    override fun findByUsername(username: String): Mono<UserDetails> =
        mono {userService.findByUsername(username)?.let { user -> AuthUser.from(user) } }
}