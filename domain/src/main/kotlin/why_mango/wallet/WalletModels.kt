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
    val uid: Long,
    val apiProvider: ApiProvider,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
) {
    init {
        require(appKey.isNotBlank()) { "appKey must not be blank" }
        require(appSecret.isNotBlank()) { "appSecret must not be blank" }
    }
}

data class WalletModel(
    val id: Long,
    val uid: Long,
    val apiProvider: ApiProvider,
    val status: Status,
    val appKey: String,
    val appSecret: String,
    val additionalInfo: AdditionalInfo,
    val securities: Map<String, WalletSecurityModel>?,
    val memo: String?,
    var beginningAssets: BigDecimal,
    var endingAssets: BigDecimal,
    var depositsDuringPeriod: BigDecimal,
    var withdrawalsDuringPeriod: BigDecimal,
    val lastSyncedAt: LocalDateTime,
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
    val lastSyncedAt: LocalDateTime,
)

@Serializable
sealed class AdditionalInfo

@Serializable
@SerialName("UPBIT")
data object UpbitAdditionalInfo : AdditionalInfo()