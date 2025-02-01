package why_mango.strategy.indicator

import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode
import kotlin.math.atan
import kotlin.math.pow

/**
 * Calculate Exponential Moving Average (EMA) of the given list of prices.
 *
 * @param period EMA 계산에 사용될 기간 (양의 정수)
 * @param scale 내부 계산에 사용할 소수점 자리수 (기본값: 10)
 * @param finalScale 최종 결과를 반올림할 소수점 자리수 (예: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return EMA 계산 결과 리스트
 *         (결과 개수 = 입력 개수 - period + 1)
 */
fun List<BigDecimal>.ema(
    period: Int,
    scale: Int = 10,
    finalScale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): List<BigDecimal> {
    require(period > 0) { "Period must be positive" }
    if (this.size < period) return emptyList()

    val result = mutableListOf<BigDecimal>()
    // 초기 EMA는 첫 period 값들의 단순 평균(SMA)
    var ema = this.take(period)
        .reduce(BigDecimal::add)
        .divide(BigDecimal(period), scale, roundingMode)
    result.add(ema.setScale(finalScale, roundingMode))

    // 이후 각 가격에 대해 EMA 업데이트: (prevEMA * (period-1) + price) / period
    for (price in this.drop(period)) {
        ema = (ema.multiply(BigDecimal(period - 1))
            .add(price))
            .divide(BigDecimal(period), scale, roundingMode)
        result.add(ema.setScale(finalScale, roundingMode))
    }
    return result
}

fun List<BigDecimal>.emaSingle(
    period: Int,
    scale: Int = 10,
    finalScale: Int = 8,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): BigDecimal? = this.ema(period, scale, finalScale, roundingMode).lastOrNull()

/**
 * Calculate Simple Moving Average (SMA) of the given list of prices.
 */
fun List<BigDecimal>.sma(
    period: Int,
    scale: Int = 10,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): BigDecimal? {
    require(period > 0) { "Period must be positive" }
    if (this.size < period) return null

    val sma = this.takeLast(period)
        .reduce(BigDecimal::add)
        .divide(BigDecimal(period), scale, roundingMode)

    return sma
}

/**
 * JMA 계산 함수 (간략 버전)
 * Pine Script 알고리즘을 Double 기반으로 구현
 *
 * @param length JMA 계산 기간 (예: 10)
 * @param phase  위상 값 (예: 50)
 * @param power  지수(power) 값 (예: 1)
 * @param scale  내부 계산 정밀도 (기본: 10)
 * @param finalScale 최종 반올림 자리수 (기본: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return 계산된 JMA 값 리스트
 */
fun List<BigDecimal>.jma(
    length: Int,
    phase: Int,
    power: Int,
    scale: Int = 10,
    finalScale: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): List<BigDecimal> {
    if (this.isEmpty()) return emptyList()
    val srcDoubles = this.map { it.toDouble() }
    val result = mutableListOf<Double>()
    val phaseRatio = when {
        phase < -100 -> 0.5
        phase > 100 -> 2.5
        else -> phase / 100.0 + 1.5
    }
    val beta = 0.45 * (length - 1) / (0.45 * (length - 1) + 2)
    val alpha = beta.pow(power.toDouble())
    var prevE0 = srcDoubles[0]
    var prevE1 = 0.0
    var prevE2 = 0.0
    var prevJma = srcDoubles[0]
    result.add(prevJma)
    for (i in 1 until srcDoubles.size) {
        val src = srcDoubles[i]
        val e0 = (1 - alpha) * src + alpha * prevE0
        val e1 = (src - e0) * (1 - beta) + beta * prevE1
        val e2 = (e0 + phaseRatio * e1 - prevJma) * (1 - alpha).pow(2.0) + alpha.pow(2.0) * prevE2
        val jma = e2 + prevJma
        result.add(jma)
        prevE0 = e0
        prevE1 = e1
        prevE2 = e2
        prevJma = jma
    }
    return result.map { BigDecimal(it).setScale(finalScale, roundingMode) }
}

/**
 * ATR Proxy 계산 함수
 * 입력된 List<BigDecimal> (close 값 목록)에서,
 * 최근 atrPeriod 구간의 연속값 차이의 단순 평균을 계산하여 ATR Proxy로 사용합니다.
 *
 * @param atrPeriod 계산에 사용할 기간 (기본: 14)
 * @param scale 내부 계산 정밀도 (기본: 10)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return ATR Proxy 값
 */
fun List<BigDecimal>.atrProxy(
    atrPeriod: Int = 14,
    scale: Int = 10,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): BigDecimal {
    if (this.size < 2) return ZERO
    val count = minOf(atrPeriod, this.size - 1)
    // 최근 count+1개의 데이터를 이용하여 연속값 간 차이 계산
    val diffs = this.takeLast(count + 1).windowed(2).map { (prev, curr) ->
        curr.subtract(prev).abs()
    }
    val sum = diffs.reduce { acc, d -> acc.add(d) }
    return sum.divide(BigDecimal(diffs.size), scale, roundingMode)
}

/**
 * List<BigDecimal> (close 값 목록)을 입력받아, JMA slope (각도)를 계산하는 확장 함수.
 *
 * 기본 설정:
 * - JMA 계산: length = 10, phase = 50, power = 1
 * - ATR Proxy: atrPeriod = 14
 * - threshold (거래 없음 구역 임계치): 4도 (기본)
 *
 * @param jmaLength JMA 계산 기간 (기본: 10)
 * @param phase     JMA 위상 (기본: 50)
 * @param power     JMA power 값 (기본: 1)
 * @param threshold 거래 없음(no trade) 임계치 (도 단위, 기본: 4)
 * @param atrPeriod ATR Proxy 계산에 사용할 기간 (기본: 14)
 * @param scale     내부 계산 정밀도 (기본: 10)
 * @param finalScale 최종 반올림 자리수 (기본: 2)
 * @param roundingMode 반올림 모드 (기본: HALF_UP)
 * @return JMA slope (각도, 도 단위) – 만약 계산 불가하거나 절대 기울기가 threshold 미만이면 0을 반환
 */
fun List<BigDecimal>.jmaSlope(
    jmaLength: Int = 10,
    phase: Int = 50,
    power: Int = 1,
    atrPeriod: Int = 14,
    scale: Int = 10,
    finalScale: Int = 8,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): BigDecimal? {
    // JMA 계산 (전체 리스트에 대해)
    val jmaList = this.jma(jmaLength, phase, power, scale, finalScale, roundingMode)
    if (jmaList.size < 2) return null

    // 마지막 두 JMA 값 차이
    val current = jmaList.last()
    val previous = jmaList[jmaList.size - 2]
    val diff = current.subtract(previous)

    // ATR Proxy 계산 (close 값 목록으로부터)
    val atrValue = this.atrProxy(atrPeriod, scale, roundingMode)
    if (atrValue.compareTo(ZERO) == 0) return null

    // 각도 계산: arctan(diff / atr) * (180 / π)
    val angleRadians = atan(diff.divide(atrValue, scale, roundingMode).toDouble())
    val angleDegrees = angleRadians * (180 / Math.PI)
    val angleBD = BigDecimal(angleDegrees).setScale(finalScale, roundingMode)

    return angleBD
}
