package why_mango.strategy.indicator

import why_mango.strategy.model.BollingerBand
import java.math.*


/**
 * BigDecimal의 제곱근을 계산하는 확장 함수 (Newton–Raphson 방식)
 */
fun BigDecimal.sqrt(mc: MathContext = MathContext(16, RoundingMode.HALF_UP)): BigDecimal {
    require(this >= BigDecimal.ZERO) { "음수의 제곱근은 계산할 수 없습니다." }
    if (this == BigDecimal.ZERO) return BigDecimal.ZERO

    var x = BigDecimal(Math.sqrt(this.toDouble()), mc)
    var prev: BigDecimal
    do {
        prev = x
        x = this.divide(x, mc).add(x).divide(BigDecimal(2), mc)
    } while (x != prev)
    return x
}


/**
 * 주어진 윈도우(window)에서 단순 이동평균(SMA)와 표준편차(stdDev)를 계산한다.
 */
private fun calculateSmaAndStdDev(window: List<BigDecimal>, period: Int): Pair<BigDecimal, BigDecimal> {
    val sum = window.fold(BigDecimal.ZERO) { acc, value -> acc + value }
    val sma = sum.divide(BigDecimal(period), MathContext.DECIMAL128)
    val variance = window.fold(BigDecimal.ZERO) { acc, value ->
        val diff = value - sma
        acc + diff.pow(2)
    }.divide(BigDecimal(period), MathContext.DECIMAL128)
    val stdDev = variance.sqrt(MathContext.DECIMAL128)
    return sma to stdDev
}

/**
 * 리스트의 각 슬라이딩 윈도우마다 볼린저 밴드를 계산한다.
 *
 * @param period 계산에 사용할 데이터 포인트 개수 (예: 20)
 * @param deviation 표준편차에 곱할 배수 (예: 2)
 * @return 각 윈도우에 대해 계산된 BollingerBand 리스트
 */
fun List<BigDecimal>.bollingerBands(
    period: Int,
    deviation: BigDecimal = BigDecimal.TWO,
): List<BollingerBand> {
    if (this.size < period) return emptyList()

    return this.windowed(size = period, step = 1, partialWindows = false).map { window ->
        val (sma, stdDev) = calculateSmaAndStdDev(window, period)
        val upper = sma + (deviation * stdDev)
        val lower = sma - (deviation * stdDev)
        BollingerBand(
            upper = upper.setScale(8, RoundingMode.HALF_UP),
            sma = sma.setScale(8, RoundingMode.HALF_UP),
            lower = lower.setScale(8, RoundingMode.HALF_UP)
        )
    }
}

/**
 * 리스트의 각 슬라이딩 윈도우마다 볼린저 밴드 너비를 계산한다.
 *
 * 볼린저 밴드 너비는 상단과 하단 밴드의 차이이며,
 * 상단 = sma + (deviation * stdDev), 하단 = sma - (deviation * stdDev)로 계산되므로,
 * 너비 = 2 * deviation * stdDev가 된다.
 *
 * @param period 계산에 사용할 데이터 포인트 개수 (예: 20)
 * @param deviation 표준편차에 곱할 배수 (예: 2)
 * @return 각 윈도우에 대해 계산된 볼린저 밴드 너비 리스트
 */
fun List<BigDecimal>.bollingerBandWidths(
    period: Int,
    deviation: BigDecimal = BigDecimal.TWO,
): List<BigDecimal> {
    if (this.size < period) return emptyList()

    return this.bollingerBands(period, deviation).map { band ->
        (band.upper - band.lower) / band.sma
    }.map { it.setScale(8, RoundingMode.HALF_UP) }
}