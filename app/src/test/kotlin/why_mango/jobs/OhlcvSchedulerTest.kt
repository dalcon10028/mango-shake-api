package why_mango.jobs

import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OhlcvSchedulerTest(
    private val ohlcvScheduler: OhlcvScheduler
) : FunSpec({
    test("ohlcvDay") {
        ohlcvScheduler.ohlcvDay()
    }
})