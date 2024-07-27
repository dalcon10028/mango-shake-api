package ymango.me.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode


enum class ErrorCode(val code: String) {
    UNKNOWN("UNKNOWN"),
}

open class BaseException(
    override val message: String,
    val statusCode: HttpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    val errorCode: ErrorCode = ErrorCode.UNKNOWN,
    val details: Any? = null,
    val data: Any? = null,
    open val throwable: Throwable? = null,
) : Exception(message, throwable)