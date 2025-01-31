package why_mango.bitget.dto.websocket.push_event

import java.math.BigDecimal

data class HistoryPositionPushEvent(
    /**
     * Position ID
     */
    val posId: String,

    /**
     * Product ID
     * delivery contract reference：https://www.bitget.com/api-doc/common/release-note
     */
    val instId: String,

    /**
     * Currency of occupied margin
     */
    val marginCoin: String,

    /**
     * Margin mode
     * fixed: isolated mode
     * crossed: crossed mode
     */
    val marginMode: String,

    /**
     * Position direction
     */
    val holdSide: String,

    /**
     * Position mode
     */
    val posMode: String,

    /**
     * Average entry price
     */
    val openPriceAvg: BigDecimal,

    /**
     * Average close price
     */
    val closePriceAvg: BigDecimal,

    /**
     * Open size
     */
    val openSize: BigDecimal,

    /**
     * Close size
     */
    val closeSize: BigDecimal,

    /**
     * Realized PnL
     */
    val achievedProfits: BigDecimal,

    /**
     * Settle fee
     */
    val settleFee: BigDecimal,

    /**
     * Total open fee
     */
    val openFee: BigDecimal,

    /**
     * Total close fee
     */
    val closeFee: BigDecimal,

    /**
     * Position creation time, milliseconds format of Unix timestamp, e.g.1597026383085
     */
    val cTime: Long,

    /**
     * Lastest position update time, milliseconds format of Unix timestamp, e.g.1597026383085
     */
    val uTime: Long
)
