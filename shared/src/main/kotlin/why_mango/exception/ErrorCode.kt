package why_mango.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(val status: HttpStatus) {
    // common
    UNKNOWN(INTERNAL_SERVER_ERROR),
    ILLIGAL_STATE(INTERNAL_SERVER_ERROR),

    // market_broker
    OPEN_API_AUTH_ERROR(UNAUTHORIZED),
    UPBIT_ERROR(INTERNAL_SERVER_ERROR),
}