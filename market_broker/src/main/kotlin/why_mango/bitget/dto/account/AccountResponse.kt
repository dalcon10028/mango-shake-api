package why_mango.bitget.dto.account

import why_mango.bitget.enums.*
import java.math.BigDecimal

data class AccountResponse(
    val marginCoin: String,
    val locked: BigDecimal,
    val available: BigDecimal,
    val crossedMaxAvailable: BigDecimal,
    val isolatedMaxAvailable: BigDecimal,
    val maxTransferOut: BigDecimal,
    val accountEquity: BigDecimal,
    val usdtEquity: BigDecimal,
    val btcEquity: BigDecimal,
    val crossedRiskRate: BigDecimal,
    val crossedMarginLeverage: BigDecimal,
    val isolatedLongLever: Int,
    val isolatedShortLever: Int,
    val marginMode: MarginMode,
    val posMode: PositionMode,
    val unrealizedPL: String,
    val coupon: BigDecimal,
    val crossedUnrealizedPL: BigDecimal,
    val isolatedUnrealizedPL: BigDecimal,
    val assetMode: AssetMode,
)
