package ymango.me.finance.upbit

import org.springframework.web.service.annotation.GetExchange
import ymango.me.finance.upbit.dto.UpbitApiKey


interface UpbitRestApi {

    @GetExchange("/api_keys")
    suspend fun findApiKeys(): List<UpbitApiKey>
}