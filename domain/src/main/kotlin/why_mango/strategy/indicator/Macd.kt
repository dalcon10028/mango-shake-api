package why_mango.strategy.indicator

import why_mango.strategy.enums.CrossResult
import why_mango.strategy.enums.CrossResult.*
import why_mango.strategy.model.MacdResult
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Calculate MACD (Moving Average Convergence Divergence) of the given list of prices.
 *
 * @param fastLength   단기 EMA 기간 (예: 12)
 * @param slowLength   장기 EMA 기간 (예: 26)
 * @param signalLength Signal 라인 EMA 기간 (예: 9)
 * @param scale        내부 계산에 사용할 소수점 자리수 (기본: 10)
 * @param finalScale   최종 결과 반올림 자리수 (기본: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return MACD, Signal, Histogram 값을 담은 MacdResult 객체들의 리스트
 *
 * [주의]
 * - fastEMA와 slowEMA를 각각 계산한 후, slowEMA가 사용 가능해지는 시점에 맞춰 fastEMA를 정렬합니다.
 * - MACD 라인 = alignedFastEMA - slowEMA
 * - Signal 라인은 MACD 라인의 EMA(signalLength)로 계산하며, 그 후 양쪽 길이를 맞춥니다.
 * - 최종 MACD 결과 리스트의 길이는 (MACD 시리즈 길이 - signalLength + 1) 입니다.
 */
fun List<BigDecimal>.macd(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalLength: Int = 9,
    scale: Int = 10,
    finalScale: Int = 8,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): List<MacdResult> {
    if (this.size < slowLength) return emptyList()

    val fastEMA = this.ema(fastLength, scale, finalScale, roundingMode)
    val slowEMA = this.ema(slowLength, scale, finalScale, roundingMode)

    // fastEMA는 길이가 (N - fastLength + 1), slowEMA는 (N - slowLength + 1)
    // 두 EMA를 정렬하려면 fastEMA에서 (slowLength - fastLength)개를 제거합니다.
    if (fastEMA.size < (slowLength - fastLength)) return emptyList()
    val alignedFastEMA = fastEMA.drop(slowLength - fastLength)

    // MACD 시리즈: 각 위치에서의 차이 = alignedFastEMA - slowEMA
    val macdSeries = alignedFastEMA.zip(slowEMA) { fast, slow ->
        fast.subtract(slow).setScale(finalScale, roundingMode)
    }

    // Signal 라인: MACD 시리즈에 대해 EMA(signalLength)를 계산
    if (macdSeries.size < signalLength) return emptyList()
    val signalLineFull = macdSeries.ema(signalLength, scale, finalScale, roundingMode)
    // EMA로 계산한 Signal 라인의 길이는 (macdSeries.size - signalLength + 1)
    // MACD 시리즈와 Signal 라인의 길이를 맞추기 위해, MACD 시리즈의 앞부분을 제거합니다.
    val alignedMacd = macdSeries.drop(macdSeries.size - signalLineFull.size)

    // Histogram = alignedMacd - signalLineFull
    val histogramSeries = alignedMacd.zip(signalLineFull) { macd, signal ->
        macd.subtract(signal).setScale(finalScale, roundingMode)
    }

    // 최종 MACD 결과를 각 인덱스별로 결합하여 리스트로 반환
    return alignedMacd.indices.map { i ->
        MacdResult(
            macd = alignedMacd[i],
            signal = signalLineFull[i],
            histogram = histogramSeries[i]
        )
    }
}

/**
 * Calculate MACD (Moving Average Convergence Divergence) of the given list of prices.
 *
 * @param fastLength   단기 EMA 기간 (기본: 12)
 * @param slowLength   장기 EMA 기간 (기본: 26)
 * @param signalLength Signal 라인 EMA 기간 (기본: 9)
 * @param scale        내부 계산 소수점 자리수 (기본: 10)
 * @param finalScale   최종 반올림 자리수 (기본: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return 가장 최신의 MacdResult, 계산 불가능하면 null 반환
 */
fun List<BigDecimal>.macdSingle(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalLength: Int = 9,
    scale: Int = 10,
    finalScale: Int = 8,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): MacdResult? = this.macd(fastLength, slowLength, signalLength, scale, finalScale, roundingMode).lastOrNull()

/**
 * Calculate MACD Cross signals of the given list of prices.
 *
 * Golden Cross: 윈도우 시작 macd < signal, 윈도우 끝 macd > signal
 * Dead Cross: 윈도우 시작 macd > signal, 윈도우 끝 macd < signal
 * None: 그 외
 */
fun List<BigDecimal>.macdCross(
    fastLength: Int = 12,
    slowLength: Int = 26,
    signalLength: Int = 9,
    scale: Int = 10,
    finalScale: Int = 8,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
    window: Int = 6
): List<CrossResult> {
    val macdResults = this.macd(fastLength, slowLength, signalLength, scale, finalScale, roundingMode)
    if (macdResults.size < 2) return emptyList()

    return macdResults.windowed(window).map { (prev, curr) ->
        when {
            prev.macd < prev.signal && curr.macd > curr.signal -> GOLDEN_CROSS
            prev.macd > prev.signal && curr.macd < curr.signal -> DEATH_CROSS
            else -> NONE
        }
    }
}