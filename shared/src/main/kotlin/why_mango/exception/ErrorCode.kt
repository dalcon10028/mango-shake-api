package why_mango.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(val status: HttpStatus) {
    // common
    UNKNOWN(INTERNAL_SERVER_ERROR),
    ILLIGAL_STATE(INTERNAL_SERVER_ERROR),
    ILLIGAL_ARGUMENT(BAD_REQUEST),

    // market_broker
    OPEN_API_AUTH_ERROR(UNAUTHORIZED),
    INVALID_ACCESS_KEY(UNAUTHORIZED),
    UPBIT_ERROR(INTERNAL_SERVER_ERROR),

    // domain
    RESOURCE_NOT_FOUND(NOT_FOUND),
    DUPLICATED_RESOURCE(CONFLICT),
}