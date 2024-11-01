package why_mango.config.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException


@Component
class GlobalErrorAttributes: DefaultErrorAttributes() {
    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, out Any?> {
        val errorAttributes = super.getErrorAttributes(request, options)
        val error: Throwable = super.getError(request)

        return when (error) {
            is MangoShakeException -> mutableMapOf(
                "message" to error.message,
                "errorCode" to error.errorCode,
                "status" to error.status.value(),
                "details" to error.details,
                "data" to error.data,
            )
            else -> mutableMapOf(
                "message" to error.localizedMessage,
                "errorCode" to ErrorCode.UNKNOWN,
                "status" to errorAttributes["status"] as Int,
                "details" to null,
                "data" to null,
            )
        }
    }
}