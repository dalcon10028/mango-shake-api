package why_mango.bitget.dto.place_order

import why_mango.bitget.enums.*
import java.math.BigDecimal

data class PlaceOrderRequest(
    /**
     * Trading pair, e.g. ETHUSDT
     */
    val symbol: String,

    /**
     * Product type
     * USDT-FUTURES USDT professional futures
     * COIN-FUTURES Mixed futures
     * USDC-FUTURES USDC professional futures
     * SUSDT-FUTURES USDT professional futures demo
     * SCOIN-FUTURES Mixed futures demo
     * SUSDC-FUTURES USDC professional futures demo
     */
    val productType: ProductType = ProductType.SUSDT_FUTURES,

    /**
     * Position mode
     * isolated: isolated margin
     * crossed: crossed margin
     */
    val marginMode: MarginMode,

    /**
     * Margin coin(capitalized)
     */
    val marginCoin: String,

    /**
     * Amount (base coin)
     * To get the decimal places of size:Get Contract Config
     */
    val size: BigDecimal,

    /**
     * Price of the order.
     * Required if the "orderType" is limit
     * To get the decimal places of size:Get Contract Config
     */
    val price: BigDecimal? = null,

    /**
     * Trade side
     * buy: Buy(one-way-mode); Long position direction(hedge-mode)
     * sell: Sell(one-way-mode); Short position direction(hedge-mode)
     */
    val side: Side,

    /**
     * Trade type
     * Only required in hedge-mode
     * open: Open position
     * close: Close position
     */
    val tradeSide: TradeType? = null,

    /**
     * Order type
     * limit: limit orders
     * market: market orders
     */
    val orderType: OrderType,

    /**
     * Order expiration date.
     * Required if the orderType is limit
     * ioc: Immediate or cancel
     * fok: Fill or kill
     * gtc: Good till canceled(default value)
     * post_only: Post only
     */
    val force: TimeInForce? = TimeInForce.GTC,

    /**
     * Customize order ID
     */
    val clientOid: String? = null,

    /**
     * Whether or not to just reduce the position: YES, NO
     * Default: NO.
     * Applicable only in one-way-position mode
     */
    val reduceOnly: ReduceOnly? = ReduceOnly.NO,

    /**
     * Take-profit value
     * No take-profit is set if the field is empty.
     */
    val presetStopSurplusPrice: BigDecimal? = null,

    /**
     * Stop-loss value
     * No stop-loss is set if the field is empty.
     */
    val presetStopLossPrice: BigDecimal? = null,

    /**
     * STP Mode(Self Trade Prevention)
     * none: not setting STP(default value)
     * cancel_taker: cancel taker order
     * cancel_maker: cancel maker order
     * cancel_both: cancel both of taker and maker orders
     */
    val stpMode: StpMode? = null,
)
