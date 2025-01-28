package why_mango.bitget.dto

import why_mango.bitget.enums.*

data class BitgetWebsocketResponse<T>(
    val event : WebsocketEvent?,
    val action: WebsocketAction?,
    val arg: Arg,
    val data: T?,
    val ts: Long
)

data class Arg (
    val instType: String,
    val channel: String,
    val instId: String
)