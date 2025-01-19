package why_mango.bitget.dto

open class BitgetResponse<out T> (
    val code: String,
    val msg: String,
    val requestTime: Long,
    val data: T
)