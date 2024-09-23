package why_mango.ohlcv.repository

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DayOhlcvRepositoryTest(
    val dayOhlcvRepository: DayOhlcvRepository
) : FunSpec({
    test("test") {
        dayOhlcvRepository.findAll().collect {
            println("baseDate: ${it.baseDate}, open: ${it.open}, high: ${it.high}, low: ${it.low}, close: ${it.close}, volume: ${it.volume}")
        }
    }
})
