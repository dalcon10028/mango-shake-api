package why_mango.stat.projection

import java.math.BigDecimal
import java.time.LocalDate

data class AumProjection(
    val date: LocalDate,
    val amount: BigDecimal
)
