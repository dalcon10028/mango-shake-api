package why_mango.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity

// https://stackoverflow.com/questions/66018084/how-to-enable-spring-security-kotlin-dsl
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import why_mango.auth.AuthenticationSuccessHandler
import why_mango.auth.JwtAuthenticationFilter
import why_mango.component.environment.EnvironmentComponent


@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig(
    private val env: EnvironmentComponent,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val serverAuthenticationSuccessHandler: AuthenticationSuccessHandler,
) {

    @Bean
    suspend fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange {
                authorize(pathMatchers("/actuator/health"), permitAll)
                authorize(pathMatchers("/webjars/**"), permitAll)
                authorize(pathMatchers("/auth/**"), permitAll)
                authorize(pathMatchers("/oauth2/**"), permitAll)
                authorize(pathMatchers("/admin/**"), hasRole("ADMIN"))
                authorize(anyExchange, authenticated)
            }
            formLogin { withDefaults() }
            httpBasic { disable() }
            csrf { disable() }
            // https://velog.io/@yso8296/Spring-Security를-이용한-통합-OAuth2-소셜-로그인-기능-구현
            // https://velog.io/@rkdalstj4505/스프링-시큐리티OAuth2카카오로-로그인로그아웃-구현
            oauth2Login {
                authenticationSuccessHandler = serverAuthenticationSuccessHandler
            }
            addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            sessionManagement { SessionCreationPolicy.STATELESS }
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        // TODO: 환경 분리시 cors 설정 변경
//        configuration.allowedOrigins = buildList {
//            add("https://mango-shake-web.vercel.app")
//            if (env.isLocal()) add("http://localhost:3000")
//        }
        configuration.allowedOrigins = listOf(
            "https://*.whymango.site",
            "http://localhost:3000"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}