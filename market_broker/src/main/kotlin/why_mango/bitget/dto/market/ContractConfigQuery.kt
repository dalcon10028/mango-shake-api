package why_mango.bitget.dto.market

import why_mango.bitget.enums.ProductType

data class ContractConfigQuery(
    val symbol: String? = null,
    val productType: ProductType = ProductType.USDT_FUTURES,
)
