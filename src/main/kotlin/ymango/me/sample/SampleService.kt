package ymango.me.sample

import org.springframework.stereotype.Service
import ymango.me.sample.dto.RedWine

@Service
class SampleService(
    private val sampleRestApi: SampleRestApi
) {
    suspend fun findRedWines(): List<RedWine> {
        return sampleRestApi.findRedWines()
    }
}