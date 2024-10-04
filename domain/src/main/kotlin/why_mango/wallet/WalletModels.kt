package why_mango.wallet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WalletCreate(
    val apiProvider: String,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
)

data class WalletModel(
    val id: Long,
    val apiProvider: String,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
    val createdAt: LocalDateTime,
)

@Serializable
sealed class AdditionalInfo

@Serializable
@SerialName("UPBIT")
class UpbitAdditionalInfo : AdditionalInfo()