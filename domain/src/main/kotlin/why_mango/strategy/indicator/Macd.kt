package why_mango.strategy.indicator

import why_mango.strategy.model.MacdResult
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calculate MACD (Moving Average Convergence Divergence) of the given list of prices.
 *
 * @param fastLength   단기 EMA 기간 (일반적으로 12)
 * @param slowLength   장기 EMA 기간 (일반적으로 26)
 * @param signalLength Signal 라인 EMA 기간 (일반적으로 9)
 * @param scale        내부 계산에 사용할 소수점 자리수 (기본값: 10)
 * @param finalScale   최종 결과를 반올림할 소수점 자리수 (예: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return MacdResult (MACD, Signal, Histogram 값들을 포함)
 */
fun List<BigDecimal>.macd(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalLength: Int = 9,
    scale: Int = 10,
    finalScale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): MacdResult {
    require(fastLength in 1..<slowLength && signalLength > 0) { "Invalid periods provided." }
    if (this.size < slowLength) return MacdResult(emptyList(), emptyList(), emptyList())

    // 단기, 장기 EMA 계산
    val fastEMA = this.ema(fastLength, scale, finalScale, roundingMode)
    val slowEMA = this.ema(slowLength, scale, finalScale, roundingMode)

    // fastEMA는 길이가 (N - fastLength + 1), slowEMA는 (N - slowLength + 1)이며,
    // 두 EMA를 시간축에 맞추기 위해 fastEMA에서 앞의 (slowLength - fastLength) 개를 제거
    val alignedFastEMA = fastEMA.drop(slowLength - fastLength)

    // MACD 라인 = alignedFastEMA - slowEMA (요소별 차이)
    val macdLine = alignedFastEMA.zip(slowEMA) { fast, slow ->
        fast.subtract(slow).setScale(finalScale, roundingMode)
    }

    // Signal 라인은 MACD 라인의 EMA를 계산하여 구함
    if (macdLine.size < signalLength) return MacdResult(emptyList(), emptyList(), emptyList())
    val signalLine = macdLine.ema(signalLength, scale, finalScale, roundingMode)

    // Signal 라인 계산 시 macdLine의 앞부분은 계산에 사용되지 않으므로, 양쪽 길이를 맞춤
    val alignedMacdLine = macdLine.drop(macdLine.size - signalLine.size)

    // Histogram = MACD 라인 - Signal 라인
    val histogram = alignedMacdLine.zip(signalLine) { macd, signal ->
        macd.subtract(signal).setScale(finalScale, roundingMode)
    }

    return MacdResult(
        macd = alignedMacdLine,
        signal = signalLine,
        histogram = histogram
    )
}