package why_mango.bitget.dto.market

import java.math.BigDecimal

data class ContractConfigResponse(
    /**
     * Product name
     */
    val symbol: String,

    /**
     * Base currency
     * Specifically refers to ETH as in ETHUSDT
     */
    val baseCoin: String,

    /**
     * Quote currency
     * Specifically refers to USDT as in ETHUSDT
     */
    val quoteCoin: String,

    /**
     * Ratio of bid price to limit price
     */
    val buyLimitPriceRatio: BigDecimal,

    /**
     * Ratio of ask price to limit price
     */
    val sellLimitPriceRatio: BigDecimal,

    /**
     * Transaction fee increase ratio
     */
    val feeRateUpRatio: BigDecimal,

    /**
     * Maker rate
     */
    val makerFeeRate: BigDecimal,

    /**
     * Taker rate
     */
    val takerFeeRate: BigDecimal,

    /**
     * Opening cost increase ratio
     */
    val openCostUpRatio: BigDecimal,

    /**
     * Supported margin coins
     */
    val supportMarginCoins: List<String>,

    /**
     * Minimum opening amount (base currency)
     */
    val minTradeNum: BigDecimal,

    /**
     * price step length
     */
    val priceEndStep: BigDecimal,

    /**
     * Decimal places of the quantity
     */
    val volumePlace: BigDecimal,

    /**
     * Decimal places of the price
     */
    val pricePlace: BigDecimal,

    /**
     * Quantity multiplier, the quantity of the order must be greater than minTradeNum and is a multiple of sizeMulti.
     */
    val sizeMultiplier: BigDecimal,

    /**
     * Futures types: perpetual; delivery
     */
    val symbolType: String,

    /**
     * Minimum USDT transaction amount
     */
    val minTradeUSDT: BigDecimal,

    /**
     * Maximum number of orders held-symbol dimension
     */
    val maxSymbolOrderNum: BigDecimal,

    /**
     * Maximum number of held orders-product type dimension
     */
    val maxProductOrderNum: BigDecimal,

    /**
     * Maximum number of positions held
     */
    val maxPositionNum: BigDecimal,

    /**
     * Trading pair status
     * listed Listing symbol
     * normal trade normal
     * maintain can't open/close position
     * limit_open: can't place orders(can close position)
     * restrictedAPI:can't place orders with API
     * off: offline
     */
    val symbolStatus: String,

    /**
     * Removal time, '-1' means normal
     */
    val offTime: String,

    /**
     * Time to open positions, '-1' means normal; other values indicate that the symbol is under maintenance or to be maintained and trading is prohibited after the specified time.
     */
    val limitOpenTime: String,

    /**
     * Delivery time
     */
    val deliveryTime: String,

    /**
     * Delivery start time
     */
    val deliveryStartTime: String,

    /**
     * Delivery period
     * this_quarter current quarter
     * next_quarter second quarter
     */
    val deliveryPeriod: String,

    /**
     * Listing time
     */
    val launchTime: String,

    /**
     * Funding fee settlement cycle, hourly/every 8 hours
     */
    val fundInterval: Int,

    /**
     * minimum leverage
     */
    val minLever: Int,

    /**
     * Maximum leverage
     */
    val maxLever: Int,

    /**
     * Position limits
     */
    val posLimit: BigDecimal,

    /**
     * Maintenance time (there will be a value when the status is under maintenance/upcoming maintenance)
     */
    val maintainTime: String,
)
