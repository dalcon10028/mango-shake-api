package why_mango.bitget.dto.websocket.push_event

import java.math.BigDecimal

data class TickerPushEvent(
    /**
     * Product ID, BTCUSDT
     */
    val instId: String,

    /**
     * Latest price
     */
    val lastPr: BigDecimal,

    /**
     * Ask price
     */
    val askPr: BigDecimal,

    /**
     * Bid price
     */
    val bidPr: BigDecimal,

    /**
     * 24h high
     */
    val high24h: BigDecimal,

    /**
     * 24h low
     */
    val low24h: BigDecimal,

    /**
     * 24h change
     */
    val change24h: BigDecimal,

    /**
     * Funding rate
     */
    val fundingRate: BigDecimal,

    /**
     * Next funding rate settlement time, Milliseconds format of timestamp Unix, e.g. 1597026383085
     */
    val nextFundingTime: Long,

    /**
     * System time, Milliseconds format of current data timestamp Unix, e.g. 1597026383085
     */
    val ts: Long,

    /**
     * Mark price
     */
    val markPrice: BigDecimal,

    /**
     * Index price
     */
    val indexPrice: BigDecimal,

    /**
     * Open interest
     */
    val holdingAmount: BigDecimal,

    /**
     * Trading volume of the coin
     */
    val baseVolume: BigDecimal,

    /**
     * Trading volume of quote currency
     */
    val quoteVolume: BigDecimal,

    /**
     * Price at 00:00 (UTC)
     */
    val openUtc: BigDecimal,

    /**
     * SymbolType: 1->perpetual 2->delivery
     */
    val symbolType: Int,

    /**
     * Trading pair
     */
    val symbol: String,

    /**
     * Delivery price of the delivery futures, when symbolType = 1(perpetual) it is always 0
     * It will be pushed 1 hour before delivery
     */
    val deliveryPrice: BigDecimal,

    /**
     * Buying amount
     */
    val bidSz: BigDecimal,

    /**
     * Selling amount
     */
    val askSz: BigDecimal,

    /**
     * Entry price of the last 24 hours, The opening time is compared on a 24-hour basis. i.e.: Now it is 7:00 PM of the 2nd day of the month, then the corresponding opening time is 7:00 PM of the 1st day of the month.
     */
    val open24h: BigDecimal

)
