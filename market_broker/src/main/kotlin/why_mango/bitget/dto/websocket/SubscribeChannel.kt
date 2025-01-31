package why_mango.bitget.dto.websocket

import why_mango.bitget.enums.ProductType
import why_mango.bitget.enums.WebsocketChannel

data class SubscribeChannel (
    /**
     * Product type
     */
    val instType: ProductType,

    /**
     * Channel name
     */
    val channel: WebsocketChannel,

    /**
     * Product ID
     */
    val instId: String,
)


class SubscribeChannelBuilder {
    private val channels = mutableListOf<SubscribeChannel>()

    fun channel(instType: ProductType = ProductType.SUSDT_FUTURES, channel: WebsocketChannel, instId: String) {
        channels.add(SubscribeChannel(instType, channel, instId))
    }

    fun build(): BitgetSubscribeRequest = BitgetSubscribeRequest(
        args = channels
    )
}

fun subscribeChannels(init: SubscribeChannelBuilder.() -> Unit): BitgetSubscribeRequest {
    return SubscribeChannelBuilder().apply(init).build()
}