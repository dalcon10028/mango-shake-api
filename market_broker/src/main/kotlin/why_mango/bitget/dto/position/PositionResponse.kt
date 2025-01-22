package why_mango.bitget.dto.position

import why_mango.bitget.enums.*
import java.math.BigDecimal

data class PositionResponse(
    /**
     * Trading pair name
     */
    val symbol: String,

    /**
     * Margin coin
     */
    val marginCoin: String,

    /**
     * Position direction
     * long: long position
     * short: short position
     */
    val holdSide: PositionDirection,

    /**
     * Amount to be filled of the current order (base coin)
     */
    val openDelegateSize: BigDecimal,

    /**
     * Margin amount (margin coin)
     */
    val marginSize: BigDecimal,

    /**
     * Available amount for positions (base currency)
     */
    val available: BigDecimal,

    /**
     * Frozen amount in the position (base currency)
     */
    val locked: BigDecimal,

    /**
     * Total amount of all positions (available amount + locked amount)
     */
    val total: BigDecimal,

    /**
     * Leverage
     */
    val leverage: Int,

    /**
     * Realized PnL(exclude the funding fee and transaction fee)
     */
    val achievedProfits: BigDecimal,

    /**
     * Average entry price
     */
    val openPriceAvg: BigDecimal,

    /**
     * Margin mode
     * isolated: isolated margin
     * crossed: cross margin
     */
    val marginMode: MarginMode,

    /**
     * Position mode
     * one_way_mode positions in one-way mode
     * hedge_mode positions in hedge-mode
     */
    val posMode: PositionMode,

    /**
     * Unrealized PnL
     */
    val unrealizedPL: BigDecimal,

    /**
     * Estimated liquidation price
     * If the value <= 0, it means the position is at low risk and there is no liquidation price at this time
     */
    val liquidationPrice: BigDecimal,

    /**
     * Tiered maintenance margin rate
     */
    val keepMarginRate: BigDecimal,

    /**
     * Mark price
     */
    val markPrice: BigDecimal,

    /**
     * Maintenance margin rate (MMR), 0.1 represents 10%
     */
    val marginRatio: BigDecimal,

    /**
     * Position breakeven price
     */
    val breakEvenPrice: BigDecimal,

    /**
     * Funding fee, the accumulated value of funding fee during the position,The initial value is empty, indicating that no funding fee has been charged yet.
     */
    val totalFee: BigDecimal,

    /**
     * Take profit price
     */
    val takeProfit: BigDecimal,

    /**
     * Stop loss price
     */
    val stopLoss: BigDecimal,

    /**
     * Take profit order ID
     */
    val takeProfitId: String,

    /**
     * Stop loss order ID
     */
    val stopLossId: String,

    /**
     * Deducted transaction fees: transaction fees deducted during the position
     */
    val deductedFee: BigDecimal,

    /**
     * Creation time, timestamp, milliseconds
     * The set is in descending order from the latest time.
     */
    val cTime: Long,

    /**
     * single : single asset mode
     * union multi-Assets mode
     */
    val assetMode: AssetMode,

    /**
     * Last updated time, timestamp, milliseconds
     */
    val uTime: Long,

)
