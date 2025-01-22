package why_mango.bitget.dto.market

import why_mango.bitget.enums.ProductType

data class TickerQuery(
    /**
     * Trading pair
     */
    val symbol: String,
    /**
     * The type of contract
     */
    val productType: ProductType = ProductType.SUSDT_FUTURES,
)
