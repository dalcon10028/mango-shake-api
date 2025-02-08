package why_mango.bitget.dto.websocket.push_event

import why_mango.bitget.enums.WebsocketAction

interface BitgetPushEvent {
    val action: WebsocketAction
}