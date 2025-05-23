package why_mango.bitget.dto.websocket

import why_mango.bitget.enums.ProductType
import why_mango.bitget.enums.WebsocketChannel

data class SubscribeChannel (
    /**
     * Product type
     */
    val instType: ProductType = ProductType.USDT_FUTURES,

    /**
     * Channel name
     */
    val channel: String,

    /**
     * Product ID
     */
    val instId: String,
)


class SubscribeChannelBuilder {
    private val channels = mutableListOf<SubscribeChannel>()

    fun channel(instType: ProductType, channel: String, instId: String) {
        channels.add(SubscribeChannel(instType, channel, instId))
    }

    fun build(): BitgetSubscribeRequest = BitgetSubscribeRequest(
        args = channels
    )
}

fun subscribeChannels(init: SubscribeChannelBuilder.() -> Unit): BitgetSubscribeRequest {
    return SubscribeChannelBuilder().apply(init).build()
}