package ymango.me.finance.upbit

import org.springframework.stereotype.Service

@Service
class UpbitService(
    private val upbitRestApi: UpbitRestApi
) {

    suspend fun findApiKeys() {
        //thread name
        println("run ${Thread.currentThread().name}")
        upbitRestApi.findApiKeys()
    }


}