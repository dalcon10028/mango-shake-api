package why_mango.strategy.indicator

import java.math.BigDecimal
import java.math.BigDecimal.*
import java.math.RoundingMode

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