package why_mango.bitget.dto.account

import why_mango.bitget.enums.ProductType

data class AccountQuery(
    /**
     * Trading pair
     */
    val symbol: String,
    /**
     * The type of contract
     */
    val productType: ProductType = ProductType.USDT_FUTURES,
    /**
     * Margin coin
     */
    val marginCoin: String = "usdt",
)
