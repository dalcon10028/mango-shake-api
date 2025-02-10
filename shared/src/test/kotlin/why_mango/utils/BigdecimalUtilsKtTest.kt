package why_mango.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale

class BigDecimalUtilsKtTest: StringSpec({
    "takeProfit calculates correct take profit price" {
        val price = "100".toBigDecimal()
        val takeProfitPrice = price.takeProfitLong(0.1, 10)
        takeProfitPrice shouldBeEqualIgnoringScale "101.0".toBigDecimal()
    }

    "stopLoss calculates correct stop loss price" {
        val price = "100".toBigDecimal()
        val stopLossPrice = price.stopLossLong(0.1, 10)
        stopLossPrice shouldBeEqualIgnoringScale "99.0".toBigDecimal()
    }

    "takeProfitShort calculates correct take profit price" {
        val price = "100".toBigDecimal()
        val takeProfitPrice = price.takeProfitShort(0.1, 10)
        takeProfitPrice shouldBeEqualIgnoringScale "99.0".toBigDecimal()
    }

    "stopLossShort calculates correct stop loss price" {
        val price = "100".toBigDecimal()
        val stopLossPrice = price.stopLossShort(0.1, 10)
        stopLossPrice shouldBeEqualIgnoringScale "101.0".toBigDecimal()
    }
})