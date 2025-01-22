package why_mango.bitget.dto.place_order

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
