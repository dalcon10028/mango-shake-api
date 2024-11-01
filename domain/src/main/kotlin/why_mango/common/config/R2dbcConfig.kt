package why_mango.common.config

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import why_mango.wallet.entity.*

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = ["why_mango"])
class R2dbcConfig(
    private val r2dbcProperties: R2dbcProperties,
) : AbstractR2dbcConfiguration() {
    override fun connectionFactory(): ConnectionFactory =
        PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(r2dbcProperties.host)
                .port(r2dbcProperties.port)
                .database(r2dbcProperties.database)
                .username(r2dbcProperties.username)
                .password(r2dbcProperties.password)
                .build()
        )

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        return R2dbcCustomConversions(
            storeConversions,
            listOf(
                AdditionalInfoWritingConverter,
                AdditionalInfoReadingConverter,
            ))
    }

    @ConfigurationProperties(prefix = "spring.r2dbc")
    data class R2dbcProperties(
        val host: String,
        val port: Int,
        val database: String,
        var username: String,
        var password: String,
    )
}