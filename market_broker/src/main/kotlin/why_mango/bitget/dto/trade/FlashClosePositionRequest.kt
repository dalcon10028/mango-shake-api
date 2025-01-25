package why_mango.bitget.dto.trade

import why_mango.bitget.enums.PositionDirection
import why_mango.bitget.enums.ProductType

data class FlashClosePositionRequest(
    /**
     * Trading pair
     */
    val symbol: String,

    /**
     * Position direction
     * 1. In one-way position mode(buy or sell): This field should be left blank. Will be ignored if filled in.
     * 2. In hedge-mode position(open or close): All positions will be closed if the field is left blank; Positions of the specified direction will be closed is the field is filled in.
     * long: Long position; short: Short position
     */
    val holdSide: PositionDirection? = null,

    /**
     * Product type
     * USDT-FUTURES USDT professional futures
     * COIN-FUTURES Mixed futures
     * USDC-FUTURES USDC professional futures
     * SUSDT-FUTURES USDT professional futures demo
     * SCOIN-FUTURES Mixed futures demo
     * SUSDC-FUTURES USDC professional futures demo
     */
    val productType: ProductType
)
