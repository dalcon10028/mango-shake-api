package why_mango.strategy.machines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import why_mango.strategy.bollinger_bands_trend.BollingerBandTrendTradingMachine

@SpringBootTest
class BollingerBandTradingMachineTest(
    private val bollingerBandTradingMachine: BollingerBandTrendTradingMachine
) : FunSpec({

    test("orderSize") {
        bollingerBandTradingMachine.orderSize("ETHUSDT", 2202.96.toBigDecimal()) shouldBe 0.22.toBigDecimal()
    }
})
