package why_mango.strategy.model

import java.math.BigDecimal

data class Position(
    val symbol: String,
    val side: String,
    val size: BigDecimal,
    val entryPrice: BigDecimal,
    val stopLoss: BigDecimal,
    val takeProfit: BigDecimal? = null,
)
