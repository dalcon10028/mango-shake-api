package why_mango.bitget.dto.websocket.push_event

import java.math.BigDecimal
import kotlin.math.min

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

    /**
     * 캔들 고가 저가 사이에 가격이 있는지 확인
     */
    fun between(price: BigDecimal): Boolean = price in low..high

    fun isBull(): Boolean = open < close

    fun isBear(): Boolean = open > close

    // 도지 여부 = 바디 길이 / 캔들 길이 < 0.2
    fun isDoji() = body / length < BigDecimal("0.2")

    val body: BigDecimal
        get() = (close - open).abs()

    val length: BigDecimal
        get() = high - low

    val lowerShadow: BigDecimal
        get() = (minOf(open, close) - low).abs()

    val upperShadow: BigDecimal
        get() = (maxOf(open, close) - high).abs()

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
