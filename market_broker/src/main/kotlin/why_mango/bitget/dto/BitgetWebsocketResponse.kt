package why_mango.bitget.dto

import why_mango.bitget.enums.*

data class BitgetWebsocketResponse<T>(
    val event : String?,
    val action: WebsocketAction?,
    val arg: Arg,
    val data: T?,
    val ts: Long,
    val code: Int?,
    val msg: String?
)

data class Arg (
    val instType: String,
    val channel: String,
    val instId: String
)