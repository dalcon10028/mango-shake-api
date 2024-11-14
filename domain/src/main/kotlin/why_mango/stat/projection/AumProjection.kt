package why_mango.stat.projection

import java.math.BigDecimal
import java.time.LocalDate

data class AumProjection(
    val baseDate: LocalDate,
    val assetValuation: BigDecimal
)
