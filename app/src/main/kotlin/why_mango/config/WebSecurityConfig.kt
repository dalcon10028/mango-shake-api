package why_mango.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity

// https://stackoverflow.com/questions/66018084/how-to-enable-spring-security-kotlin-dsl
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import why_mango.auth.AuthenticationSuccessHandler
import why_mango.auth.JwtAuthenticationFilter
import why_mango.component.environment.EnvironmentComponent
import why_mango.user.enums.Privilege
import why_mango.user.enums.Role


@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig(
    private val env: EnvironmentComponent,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val serverAuthenticationSuccessHandler: AuthenticationSuccessHandler,
    @Value("\${app.domain}") private val appDomain: String,
) {

    @Bean
    suspend fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange {
                authorize(pathMatchers("/actuator/health"), permitAll)
                authorize(pathMatchers("/webjars/**"), permitAll)
                authorize(pathMatchers("/v3/api-docs/**"), permitAll)
                authorize(pathMatchers("/auth/**"), permitAll)
                authorize(pathMatchers("/bitget/**"), permitAll)
                authorize(pathMatchers("/oauth2/**"), permitAll)
                authorize(pathMatchers("/stats/**"), hasRole(Privilege.COMMUNITY_READ.name))
                authorize(pathMatchers("/admin/**"), hasRole(Role.ADMIN.name))
                authorize(anyExchange, authenticated)
            }
            formLogin { withDefaults() }
            httpBasic { disable() }
            csrf { disable() }
            cors { corsConfigurationSource() }
            // https://velog.io/@yso8296/Spring-Security를-이용한-통합-OAuth2-소셜-로그인-기능-구현
            // https://velog.io/@rkdalstj4505/스프링-시큐리티OAuth2카카오로-로그인로그아웃-구현
            oauth2Login {
                authenticationSuccessHandler = serverAuthenticationSuccessHandler
            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            sessionManagement { SessionCreationPolicy.STATELESS }
        }
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration().also {
            it.addAllowedOriginPattern("*$appDomain*")
            it.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            it.allowedHeaders = listOf("*")
            it.allowCredentials = true
            it.maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().also { it.registerCorsConfiguration("/**", configuration) }
    }
}