package why_mango.configs

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import why_mango.exception.MangoShakeException


@Component
class GlobalErrorAttributes: DefaultErrorAttributes() {
    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, options)
        val error: Throwable = super.getError(request)

        if (error is MangoShakeException) {
            errorAttributes["message"] = error.message
            errorAttributes["errorCode"] = error.errorCode
            errorAttributes["status"] = error.status.value()
            errorAttributes["details"] = error.details
            errorAttributes["data"] = error.data
        } else {
            errorAttributes["message"] = error.message
            errorAttributes["errorCode"] = "unknown"
            errorAttributes["status"] = 500
            errorAttributes["details"] = null
            errorAttributes["data"] = null
        }

        return errorAttributes
    }
}