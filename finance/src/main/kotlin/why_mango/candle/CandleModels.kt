package why_mango.candle

import java.math.BigDecimal
import java.time.LocalDate

data class DayCandleModel(
    val baseDate: LocalDate,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
)