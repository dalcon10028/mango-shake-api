package why_mango.bitget.dto

data class BitgetResponse<out T> (
    val code: String,
    val msg: String,
    val requestTime: Long,
    val data: T
)