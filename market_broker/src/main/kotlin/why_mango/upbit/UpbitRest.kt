package why_mango.upbit

import feign.RequestLine
import why_mango.upbit.dto.ApiKeyResponse

interface UpbitRest {
    @RequestLine("GET /api_keys")
    suspend fun getApiKeys(): List<ApiKeyResponse>
}