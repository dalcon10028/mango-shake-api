package why_mango.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.session.*
import org.springframework.security.web.server.authentication.logout.*
import org.springframework.security.core.userdetails.*

// https://stackoverflow.com/questions/66018084/how-to-enable-spring-security-kotlin-dsl
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import why_mango.component.environment.EnvironmentComponent


@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig(
    private val env: EnvironmentComponent,
) {
    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val user: UserDetails = User.builder()
            .username("user")
            .password("{noop}user")
            .roles("USER")
            .build()
        return MapReactiveUserDetailsService(user)
    }

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
            formLogin { disable() }
            httpBasic { withDefaults() }
            csrf { disable() }
//            oauth2Login {}
            sessionManagement { SessionCreationPolicy.STATELESS }
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = buildList {
//            add("https://mango-shake-web.vercel.app")
//            if (env.isLocal()) add("http://localhost:3000")
//        }
        configuration.allowedOrigins = listOf(
            "https://mango-shake-web.vercel.app",
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