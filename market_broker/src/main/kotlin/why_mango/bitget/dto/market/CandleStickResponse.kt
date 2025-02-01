package why_mango.bitget.dto.market

import java.math.BigDecimal

data class CandleStickResponse(
    /**
     * Milliseconds format of timestamp Unix, e.g. 1597026383085
     */
    val timestamp: Long,
    /**
     * Open price
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
     * Close price
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
