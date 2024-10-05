package why_mango.wallet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import why_mango.enums.ApiProvider
import why_mango.enums.Currency
import why_mango.wallet.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class WalletCreate(
    val apiProvider: ApiProvider,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
)

data class WalletModel(
    val id: Long,
    val apiProvider: ApiProvider,
    val status: Status,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
    val securities: Map<String, WalletSecurityModel>,
    val memo: String?,
    val createdAt: LocalDateTime,
)

data class WalletSecurityModel(
    val id: Long,
    val walletId: Long,
    val symbol: String,
    val currency: Currency,
    val balance: BigDecimal,
    val locked: BigDecimal,
    val averageBuyPrice: BigDecimal,
    val createdAt: LocalDateTime,
)

@Serializable
sealed class AdditionalInfo

@Serializable
@SerialName("UPBIT")
data object UpbitAdditionalInfo : AdditionalInfo()