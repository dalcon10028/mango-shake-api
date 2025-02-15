package why_mango.strategy.model

import java.math.BigDecimal
import java.math.RoundingMode

data class BollingerBand(
    val upper: BigDecimal, // upper = middle + 2 * standard deviation
    val sma: BigDecimal, // middle is the same as the simple moving average
    val lower: BigDecimal // lower = middle - 2 * standard deviation
) {
    val width: BigDecimal
        get() = ((upper - lower) / sma).setScale(8, RoundingMode.HALF_UP)
}
