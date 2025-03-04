package why_mango.strategy.machines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
class BollingerBandTradingMachineTest(
    private val bollingerBandTradingMachine: BollingerBandTradingMachine
) : FunSpec({

    test("orderSize") {
        bollingerBandTradingMachine.orderSize("ETHUSDT", 2202.96.toBigDecimal()) shouldBe 0.22.toBigDecimal()
    }
})
