package why_mango.strategy.indicator

import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import java.math.*

/**
 * 리스트의 각 슬라이딩 윈도우마다 Money Flow Index(MFI)를 계산한다.
 *
 * MFI 계산 과정:
 * 1. 각 캔들에 대해 Typical Price = (High + Low + Close) / 3 계산
 * 2. Raw Money Flow = Typical Price × Volume 계산
 * 3. 두 번째 캔들부터 전일의 Typical Price와 비교하여, 상승하면 Positive, 하락하면 Negative Money Flow에 누적
 * 4. Money Flow Ratio = (Positive Money Flow / Negative Money Flow)
 * 5. MFI = 100 - (100 / (1 + Money Flow Ratio))
 *
 * @param period 계산에 사용할 캔들 개수 (예: 14)
 * @return 각 슬라이딩 윈도우에 대해 계산된 MFI 값의 리스트
 */
fun List<CandleStickPushEvent>.moneyFlowIndex(
    period: Int = 14,
): List<BigDecimal> {
    if (this.size <= period) {
        return emptyList()
    }

    return this.windowed(size = period + 1, step = 1, partialWindows = false).map { window ->
        // 각 캔들에 대한 Typical Price 계산
        val typicalPrices = window.map { candle ->
            (candle.high + candle.low + candle.close)
                .divide(BigDecimal(3), MathContext.DECIMAL128)
        }

        var positiveFlow = BigDecimal.ZERO
        var negativeFlow = BigDecimal.ZERO

        // 첫 번째 캔들은 비교 대상이 없으므로 두 번째 캔들부터 진행
        for (i in 1 until window.size) {
            val currentTP = typicalPrices[i]
            val previousTP = typicalPrices[i - 1]
            val rawFlow = currentTP.multiply(window[i].volume, MathContext.DECIMAL128)

            when {
                currentTP.compareTo(previousTP) > 0 -> positiveFlow += rawFlow
                currentTP.compareTo(previousTP) < 0 -> negativeFlow += rawFlow
                // 동일하면 아무 처리도 하지 않음
            }
        }

        // Negative Money Flow가 0이면 MFI는 100으로 처리
        if (negativeFlow.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal(100)
        } else {
            val ratio = positiveFlow.divide(negativeFlow, 8, RoundingMode.HALF_UP)
            val onePlusRatio = BigDecimal.ONE.add(ratio)
            BigDecimal(100) - (BigDecimal(100).divide(onePlusRatio, 8, RoundingMode.HALF_UP))
        }
    }
}