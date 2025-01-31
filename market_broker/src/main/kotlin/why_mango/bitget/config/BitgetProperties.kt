package why_mango.bitget.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ConfigurationProperties(prefix = "bitget")
@EnableConfigurationProperties(BitgetProperties::class)
data class BitgetProperties(
    val baseUrl: String,
    val websocketPublicUrl: String,
    val websocketPrivateUrl: String,
    val passphrase: String,
    val accessKey: String,
    val secretKey: String,
)
