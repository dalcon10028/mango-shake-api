package why_mango.strategy.machines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import why_mango.strategy.bollinger_bands_trend.BollingerBandTrendTradingMachine

@SpringBootTest
class BollingerBandTradingMachineTest(
    private val bollingerBandTradingMachine: BollingerBandTrendTradingMachine
) : FunSpec({

    test("orderSize ETH") {
        bollingerBandTradingMachine.orderSize("ETHUSDT", 2202.96.toBigDecimal()) shouldBe 0.22.toBigDecimal()
    }

//    test("orderSize PEPE") {
//        bollingerBandTradingMachine.orderSize("PEPEUSDT", 0.000012345678901234.toBigDecimal()) shouldBe 4050000036.4501.toBigDecimal()
//    }
})
