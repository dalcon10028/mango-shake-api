package why_mango.bitget.dto.market

import java.math.BigDecimal

/**
 * > symbol	String	Trading pair name
 * > lastPr	String	Last price
 * > askPr	String	Ask price
 * > bidPr	String	Bid price
 * > bidSz	String	Buying amount
 * > askSz	String	Selling amount
 * > high24h	String	24h high
 * > low24h	String	24h low
 * > ts	String	Milliseconds format of current data timestamp Unix, e.g. 1597026383085
 * > change24h	String	Price increase or decrease (24 hours)
 * > baseVolume	String	Trading volume of the coin
 * > quoteVolume	String	Trading volume of quote currency
 * > usdtVolume	String	Trading volume of USDT
 * > openUtc	String	UTC0 opening price
 * > changeUtc24h	String	UTC0 24-hour price increase and decrease
 * > indexPrice	String	Index price
 * > fundingRate	String	Funding rate
 * > holdingAmount	String	Current holding positions(base coin)
 * > open24h	String	Entry price of the last 24 hours
 * The opening time is compared on a 24-hour basis. i.e.: Now it is 7:00 PM of the 2nd day of the month, then the corresponding opening time is 7:00 PM of the 1st day of the month.
 * > deliveryStartTime	String	Delivery start time (only for delivery contracts)
 * > deliveryTime	String	Delivery time (only for delivery contracts）
 * > deliveryStatus	String	Delivery status (only for delivery contracts; delivery_config_period: Newly listed currency pairs are configured
 * delivery_normal: Trading normally
 * delivery_before: 10 minutes before delivery, opening positions are prohibited
 * delivery_period: Delivery, opening, closing, and canceling orders are prohibited
 * > markPrice	String	Mark price
 */

data class TickerResponse(
    /**
     * Trading pair name
     */
    val symbol: String,

    /**
     * Last price
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
     * Buying amount
     */
    val bidSz: BigDecimal,

    /**
     * Selling amount
     */
    val askSz: BigDecimal,

    /**
     * 24h high
     */
    val high24h: BigDecimal,

    /**
     * 24h low
     */
    val low24h: BigDecimal,

    /**
     * Milliseconds format of current data timestamp Unix, e.g. 1597026383085
     */
    val ts: String,

    /**
     * Price increase or decrease (24 hours)
     */
    val change24h: BigDecimal,

    /**
     * Trading volume of the coin
     */
    val baseVolume: BigDecimal,

    /**
     * Trading volume of quote currency
     */
    val quoteVolume: BigDecimal,

    /**
     * Trading volume of USDT
     */
    val usdtVolume: BigDecimal,

    /**
     * UTC0 opening price
     */
    val openUtc: BigDecimal,

    /**
     * UTC0 24-hour price increase and decrease
     */
    val changeUtc24h: BigDecimal,

    /**
     * Index price
     */
    val indexPrice: BigDecimal,

    /**
     * Funding rate
     */
    val fundingRate: BigDecimal,

    /**
     * Current holding positions(base coin)
     */
    val holdingAmount: BigDecimal,

    /**
     * Entry price of the last 24 hours
     */
    val open24h: BigDecimal,

    /**
     * The opening time is compared on a 24-hour basis. i.e.: Now it is 7:00 PM of the 2nd day of the month, then the corresponding opening time is 7:00 PM of the 1st day of the month.
     */
    val deliveryStartTime: String,

    /**
     * Delivery time (only for delivery contracts）
     */
    val deliveryTime: String,

    /**
     * Delivery status (only for delivery contracts; delivery_config_period: Newly listed currency pairs are configured
     * delivery_normal: Trading normally
     * delivery_before: 10 minutes before delivery, opening positions are prohibited
     * delivery_period: Delivery, opening, closing, and canceling orders are prohibited
     */
    val deliveryStatus: String,

    /**
     * Mark price
     */
    val markPrice: BigDecimal,

)
