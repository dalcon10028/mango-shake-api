package why_mango.strategy.model

import java.math.BigDecimal

data class MacdResult(
    val macd: BigDecimal,
    val signal: BigDecimal,
    val histogram: BigDecimal,
)
