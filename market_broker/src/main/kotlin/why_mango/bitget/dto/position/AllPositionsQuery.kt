package why_mango.bitget.dto.position

import why_mango.bitget.enums.ProductType

data class AllPositionsQuery(
    val productType: ProductType,
    val marginCoin: String? = null
)
