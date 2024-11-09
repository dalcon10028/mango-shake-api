package why_mango.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(val status: HttpStatus) {
    // common
    UNKNOWN(INTERNAL_SERVER_ERROR),
    ILLEGAL_STATE(INTERNAL_SERVER_ERROR),
    ILLEGAL_ARGUMENT(BAD_REQUEST),

    // auth
    AUTHENTICATION_FAILED(UNAUTHORIZED),

    // market_broker
    OPEN_API_AUTH_ERROR(UNAUTHORIZED),
    INVALID_ACCESS_KEY(UNAUTHORIZED), // wallet 폐기 필요
    UPBIT_ERROR(INTERNAL_SERVER_ERROR),
    CODE_NOT_FOUND(NOT_FOUND),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS),

    // domain
    RESOURCE_NOT_FOUND(NOT_FOUND),
    DUPLICATED_RESOURCE(CONFLICT),
}