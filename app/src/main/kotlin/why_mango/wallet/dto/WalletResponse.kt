package why_mango.wallet.dto

import why_mango.enums.ApiProvider
import why_mango.utils.mask
import why_mango.wallet.AdditionalInfo
import why_mango.wallet.WalletModel
import why_mango.wallet.WalletSecurityModel
import why_mango.wallet.enums.Status
import java.time.LocalDateTime

data class WalletResponse(
    val id: Long,
    val apiProvider: ApiProvider,
    val status: Status,
    val appKey: String,
    val additionalInfo: AdditionalInfo,
    val securities: Map<String, WalletSecurityModel>,
    val memo: String?,
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
        createdAt = createdAt,
    )
}