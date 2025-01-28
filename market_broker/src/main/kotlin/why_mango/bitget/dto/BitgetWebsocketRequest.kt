package why_mango.bitget.dto

import why_mango.bitget.dto.websocket.SubscribeChannel

data class BitgetWebsocketRequest(
    val op: String = "subscribe",
    val args: List<SubscribeChannel>
)
