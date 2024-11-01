package why_mango.auth

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import why_mango.auth.model.AuthUser

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = resolveToken(exchange)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val user: AuthUser = jwtTokenProvider.parseToken(token)
            val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        }
        return chain.filter(exchange)
    }

    private fun resolveToken(exchange: ServerWebExchange): String? {
        val bearerToken = exchange.request.headers.getFirst(AUTHORIZATION)

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null
        }
        return bearerToken.substring(7)
    }
}