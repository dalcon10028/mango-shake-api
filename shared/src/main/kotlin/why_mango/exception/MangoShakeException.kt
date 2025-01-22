package why_mango.exception

import org.springframework.http.HttpStatus

open class MangoShakeException(
    val errorCode: ErrorCode,
    message: String,
    val status: HttpStatus = errorCode.status,
    val details: String? = null,
    val data: Any? = null,
): RuntimeException(message) {

    override fun toString(): String {
        return "MangoShakeException(message='$message', errorCode='$errorCode', status=$status, details=$details, data=$data)"
    }
}