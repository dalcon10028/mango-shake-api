package why_mango.bitget.dto.websocket.push_event

import java.math.BigDecimal

data class CandleStickPushEvent(
    /**
     * Start time, milliseconds format of Unix timestamp, e.g.1597026383085
     */
    val timestamp: Long,

    /**
     * Opening price
     */
    val open: BigDecimal,

    /**
     * Highest price
     */
    val high: BigDecimal,

    /**
     * Lowest price
     */
    val low: BigDecimal,

    /**
     * Closing price
     */
    val close: BigDecimal,

    /**
     * The value is the trading volume of left coin
     */
    val volume: BigDecimal,

    /**
     * Trading volume of quote currency
     */
    val amount: BigDecimal,

    /**
     * Trading volume of USDT
     */
    val usdtAmount: BigDecimal
) {
    companion object {
        fun from(data: List<String>): CandleStickPushEvent {
            return CandleStickPushEvent(
                timestamp = data[0].toLong(),
                open = data[1].toBigDecimal(),
                high = data[2].toBigDecimal(),
                low = data[3].toBigDecimal(),
                close = data[4].toBigDecimal(),
                volume = data[5].toBigDecimal(),
                amount = data[6].toBigDecimal(),
                usdtAmount = data[7].toBigDecimal()
            )
        }
    }
}
