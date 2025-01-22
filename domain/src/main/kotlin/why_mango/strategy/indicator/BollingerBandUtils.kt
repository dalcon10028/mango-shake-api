package why_mango.strategy.indicator

import why_mango.strategy.model.BollingerBand
import java.math.BigDecimal
import java.math.MathContext

fun List<BigDecimal>.average(): BigDecimal {
    return this.reduce { acc, bigDecimal -> acc + bigDecimal } / BigDecimal(this.size)
}

fun List<BigDecimal>.bollingerBand(deviation: BigDecimal = BigDecimal.TWO): BollingerBand {
    val sma = this.average()
    val stdDev = this.map { it - sma }
        .map { it.pow(2) }
        .average()
        .sqrt(MathContext.DECIMAL64)
    return BollingerBand(
        upper = sma + stdDev * deviation,
        lower = sma - stdDev * deviation,
        sma = sma
    )
}