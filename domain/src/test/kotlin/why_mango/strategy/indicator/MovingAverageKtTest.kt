package why_mango.strategy.indicator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class MovingAverageKtTest : StringSpec({

    "EMA single value calculation" {
        // 입력 리스트 (예제: 6개의 값 → 결과 EMA 개수는 6 - 3 + 1 = 4)
        val list = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0).map { it.toBigDecimal() }
        val result = list.ema(
            period = 3,
            scale = 10,           // 내부 계산 정밀도
            finalScale = 2,       // 최종 반올림 자리수
            roundingMode = RoundingMode.HALF_UP
        )

        // 예상 결과:
        // 초기 EMA = (1+2+3)/3 = 2.00
        // EMA[1] = ((2.00 * 2) + 4) / 3 = 8/3 = 2.66666667 → 2.67
        // EMA[2] = ((2.66666667 * 2) + 5) / 3 = 10.33333333/3 = 3.44444444 → 3.44
        // EMA[3] = ((3.44444444 * 2) + 6) / 3 = 12.88888889/3 = 4.29629630 → 4.30
        result shouldBe listOf(
            BigDecimal("2.00"),
            BigDecimal("2.67"),
            BigDecimal("3.44"),
            BigDecimal("4.30")
        )
    }

    "Empty list returns empty list" {
        val list = emptyList<BigDecimal>()
        list.ema(period = 3) shouldBe emptyList()
    }

    "List size smaller than period returns empty list" {
        val list = listOf(BigDecimal("1"), BigDecimal("2"))
        list.ema(period = 3) shouldBe emptyList()
    }

    "Period equals 1 returns original list" {
        // period가 1이면 EMA 공식에 의해 각 EMA값은 해당 가격과 동일함
        val list = listOf(
            BigDecimal("1.23"),
            BigDecimal("4.56"),
            BigDecimal("7.89")
        )
        val expected = list.map { it.setScale(2, RoundingMode.HALF_UP) }
        list.ema(period = 1, scale = 10, finalScale = 2, roundingMode = RoundingMode.HALF_UP) shouldBe expected
    }

    "Period equals list size returns SMA only" {
        // 입력 크기가 period와 동일하면 결과는 초기 SMA 1개만 나옴.
        val list = listOf(
            BigDecimal("1"),
            BigDecimal("2"),
            BigDecimal("3"),
            BigDecimal("4")
        )
        val sma = list.reduce(BigDecimal::add)
            .divide(BigDecimal(list.size), 10, RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP)
        list.ema(period = list.size, scale = 10, finalScale = 2, roundingMode = RoundingMode.HALF_UP) shouldBe listOf(sma)
    }

    "Negative period throws IllegalArgumentException" {
        val list = listOf(
            BigDecimal("1"),
            BigDecimal("2"),
            BigDecimal("3")
        )
        val exception = shouldThrow<IllegalArgumentException> {
            list.ema(period = -1)
        }
        exception.message shouldBe "Period must be positive"
    }

    "Different rounding mode (RoundingMode.DOWN) works correctly" {
        // RoundingMode.DOWN 사용 시 반올림 대신 내림 처리되는지 확인
        val list = listOf(
            BigDecimal("1.0"),
            BigDecimal("2.0"),
            BigDecimal("3.0"),
            BigDecimal("4.0"),
            BigDecimal("5.0")
        )
        val result = list.ema(
            period = 3,
            scale = 10,
            finalScale = 2,
            roundingMode = RoundingMode.DOWN
        )
        // 계산 과정:
        // 초기 SMA: (1+2+3)/3 = 2.00
        // EMA[1] = ((2.00*2)+4)/3 = (4+4)/3 = 8/3 = 2.6666666666... → 2.66 (내림)
        // EMA[2] = ((2.6666666666*2)+5)/3 ≈ 3.4444444444 → 3.44
        result shouldBe listOf(
            BigDecimal("2.00"),
            BigDecimal("2.66"),
            BigDecimal("3.44")
        )
    }
})