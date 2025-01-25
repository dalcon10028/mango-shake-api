package why_mango.bitget.dto.trade

data class FlashClosePositionResponse(
    /**
     * The collection of successfully closed orders
     */
    val successList: List<SuccessListItem>
    ,
    /**
     * The collection of unsuccessfully closed orders
     * The close order may fail when the pair is in delivery or in risk control handling
     */
    val failureList: List<FailureListItem>
)

data class SuccessListItem(
    /**
     * Order ID
     */
    val orderId: String,
    /**
     * Customize order ID
     */
    val clientOid: String,
    /**
     * The Symbol
     */
    val symbol: String
)

data class FailureListItem(
    /**
     * Order ID
     */
    val orderId: String,
    /**
     * Customize order ID
     */
    val clientOid: String,
    /**
     * The Symbol
     */
    val symbol: String,
    /**
     * Failure reason
     */
    val errorMsg: String,
    /**
     * Failure code
     */
    val errorCode: String
)