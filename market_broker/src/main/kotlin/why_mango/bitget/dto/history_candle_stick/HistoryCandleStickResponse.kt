package why_mango.bitget.dto.history_candle_stick

import java.math.BigDecimal

data class HistoryCandleStickResponse(
    /**
     * Milliseconds format of timestamp Unix, e.g. 1597026383085
     */
    val timeStamp: Long,
    /**
     * Entry price
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
     * Exit price(Only include the finished K line data)
     */
    val close: BigDecimal,
    /**
     * Trading volume of the base coin
     */
    val volume: BigDecimal,
    /**
     * Trading volume of quote currency
     */
    val amount: BigDecimal
)
