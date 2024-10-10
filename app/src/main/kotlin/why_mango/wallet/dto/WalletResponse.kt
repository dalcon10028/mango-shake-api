package why_mango.wallet.dto

import why_mango.enums.ApiProvider
import why_mango.utils.mask
import why_mango.wallet.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime
import why_mango.wallet.*

data class WalletResponse(
    val id: Long,
    val apiProvider: ApiProvider,
    val status: Status,
    val appKey: String,
    val additionalInfo: AdditionalInfo,
    val securities: Map<String, WalletSecurityModel>,
    val memo: String?,
    val beginningAssets: BigDecimal,
    val endingAssets: BigDecimal,
    val depositsDuringPeriod: BigDecimal,
    val withdrawalsDuringPeriod: BigDecimal,
    val createdAt: LocalDateTime,
)

fun WalletModel.toResponse(): WalletResponse {
    assert(securities != null) { "securities must not be null" }
    return WalletResponse(
        id = id,
        apiProvider = apiProvider,
        status = status,
        appKey = appKey.mask(),
        additionalInfo = additionalInfo,
        securities = securities!!,
        memo = memo,
        beginningAssets = beginningAssets,
        endingAssets = endingAssets,
        depositsDuringPeriod = depositsDuringPeriod,
        withdrawalsDuringPeriod = withdrawalsDuringPeriod,
        createdAt = createdAt,
    )
}