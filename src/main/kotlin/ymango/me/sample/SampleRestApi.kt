package ymango.me.sample

import org.springframework.web.service.annotation.GetExchange
import ymango.me.sample.dto.RedWine

interface SampleRestApi {

    @GetExchange("/wines/reds")
    suspend fun findRedWines(): List<RedWine>
}