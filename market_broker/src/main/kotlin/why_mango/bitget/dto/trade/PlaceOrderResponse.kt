package why_mango.bitget.dto.trade

data class PlaceOrderResponse(
    /**
     * Order ID
     */
    val orderId: String,

    /**
     * Customize order ID
     */
    val clientOid: String
)
