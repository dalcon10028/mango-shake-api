package why_mango.strategy.model

import java.math.BigDecimal

data class MacdResult(
    val macd: List<BigDecimal>,
    val signal: List<BigDecimal>,
    val histogram: List<BigDecimal>
)
