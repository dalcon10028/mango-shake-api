package why_mango.strategy.indicator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*

class MacdKtTest: StringSpec({
    "MACD calculation returns correct series lengths" {
        // 예시: 1부터 50까지의 값을 가격 데이터로 사용 (BigDecimal)
        val prices = (1..50).map { it.toBigDecimal() }
        // slowLength = 26 인 경우, slowEMA 길이는 50 - 26 + 1 = 25
        // fastEMA는 50 - 12 + 1 = 39 길이이나, 이를 맞추기 위해 앞부분 (26 - 12) = 14개를 제거하면 25 길이가 됨.
        // MACD 라인 길이는 25, signalLength = 9 인 경우 signal 라인 길이는 25 - 9 + 1 = 17
        val macdResult = prices.macd(fastLength = 12, slowLength = 26, signalLength = 9)
        macdResult.macd.size shouldBe 17
        macdResult.signal.size shouldBe 17
        macdResult.histogram.size shouldBe 17
    }

    "MACD calculation returns empty results when not enough data" {
        // 데이터가 slowLength보다 적으면 빈 결과 반환
        val prices = (1..20).map { it.toBigDecimal() }
        val macdResult = prices.macd(fastLength = 12, slowLength = 26, signalLength = 9)
        macdResult.macd shouldBe emptyList()
        macdResult.signal shouldBe emptyList()
        macdResult.histogram shouldBe emptyList()
    }

    "MACD calculation on a window of 26 values returns empty results" {
        // 제공된 예시값 (26개의 가격 데이터)
        val window = listOf(
            "3.037", "3.043", "3.038", "3.037", "3.039", "3.044", "3.042", "3.04",
            "3.042", "3.045", "3.043", "3.047", "3.044", "3.048", "3.047", "3.048",
            "3.048", "3.05", "3.048", "3.046", "3.047", "3.043", "3.048", "3.048",
            "3.059", "3.06"
        ).map { it.toBigDecimal() }

        // MACD 함수 호출: FastLength=12, SlowLength=26, SignalLength=9
        val result = window.macd(fastLength = 12, slowLength = 26, signalLength = 9)

        // 실제 계산에서는 slowEMA의 결과가 1개, fastEMA 정렬 후 MACD 라인도 1개가 되어,
        // Signal 라인을 계산할 수 없으므로 결과는 빈 리스트가 되어야 함.
        result.macd shouldBe emptyList()
        result.signal shouldBe emptyList()
        result.histogram shouldBe emptyList()
    }
})