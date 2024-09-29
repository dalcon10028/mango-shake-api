package why_mango.exception

import org.springframework.http.HttpStatus

class MangoShakeException(
    message: String,
    val errorCode: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val details: String? = null,
    val data: Any? = null,
): RuntimeException(message) {

    override fun toString(): String {
        return "MangoShakeException(message='$message', errorCode='$errorCode', status=$status, details=$details, data=$data)"
    }
}