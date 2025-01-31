package why_mango.bitget.dto.websocket

data class BitgetSubscribeRequest(
    val op: String = "subscribe",
    val args: List<SubscribeChannel>
)
