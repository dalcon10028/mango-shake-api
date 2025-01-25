package why_mango.bitget.exception

import org.springframework.http.HttpStatus
import why_mango.bitget.dto.BitgetResponse
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException

class BitgetException(
    errorCode: ErrorCode = ErrorCode.BITGET_ERROR,
    message: String,
    status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    details: String?,
    data: Any?,
) : MangoShakeException(errorCode, message, status, details, data) {
    companion object {
        private val errorMap = mapOf(
            "40008" to HttpStatus.REQUEST_TIMEOUT,
            "40009" to HttpStatus.INTERNAL_SERVER_ERROR,
            "40019" to HttpStatus.BAD_REQUEST,
            "22002" to HttpStatus.NOT_FOUND,
        )
    }

    constructor(message: String) : this(ErrorCode.BITGET_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR, null, null)
    constructor(response: BitgetResponse<*>) : this(
        ErrorCode.BITGET_ERROR,
        "Bitget error: ${response.code} ${response.msg}",
        errorMap[response.code] ?: HttpStatus.INTERNAL_SERVER_ERROR,
        response.code,
        response
    )
    constructor(errorCode: ErrorCode, message: String, status: HttpStatus) : this(errorCode, message, status, null, null)
    constructor(errorCode: ErrorCode, message: String) : this(errorCode, message, HttpStatus.INTERNAL_SERVER_ERROR, null, null)
}