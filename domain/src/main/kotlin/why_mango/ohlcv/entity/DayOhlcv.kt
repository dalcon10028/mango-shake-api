package why_mango.ohlcv.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("day_ohlcv")
class DayOhlcv(
    @Id
    val id: Long,

    val baseDate: LocalDate,

    val open: BigDecimal,

    val high: BigDecimal,

    val low: BigDecimal,

    val close: BigDecimal,

    val volume: BigDecimal,

    val createdAt: LocalDateTime,
)
