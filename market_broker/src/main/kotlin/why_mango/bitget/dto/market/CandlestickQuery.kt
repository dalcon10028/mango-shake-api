package why_mango.bitget.dto.market

import why_mango.bitget.enums.*

data class CandlestickQuery (
    /**
     * Trading pair
     */
    val symbol: String,

    /**
     * Product type
     */
    val productType: ProductType = ProductType.SUSDT_FUTURES,

    /**
     * K-line particle size
     */
    val granularity: String,

    /**
     * The start time is to query the k-lines after this time
     * According to the different time granularity, the corresponding time unit must be rounded down to be queried.
     * The millisecond format of the Unix timestamp, such as 1672410780000
     * Request data after this start time (the maximum time query range is 90 days)
     */
    val startTime: String? = null,

    /**
     * The end time is to query the k-lines before this time
     * According to the different time granularity, the corresponding time unit must be rounded down to be queried.
     * The millisecond format of the Unix timestamp, such as 1672410780000
     * Request data before this end time (the maximum time query range is 90 days)
     */
    val endTime: String? = null,

    /**
     * Candlestick chart types: MARKET tick; MARK mark; INDEX index;
     * MARKET by default
     */
    val kLineType: String? = null,

    /**
     * Default: 100, maximum: 1000
     */
    val limit: Int? = null
)