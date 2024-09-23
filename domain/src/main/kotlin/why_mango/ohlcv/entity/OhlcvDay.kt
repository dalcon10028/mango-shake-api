package why_mango.ohlcv.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import why_mango.enums.Exchange
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("ohlcv_day")
class OhlcvDay(
    @Id
    val id: Long? = null,

    @Column("base_date")
    val baseDate: LocalDate,

    @Column("exchange")
    val exchange: Exchange,

    @Column("symbol")
    val symbol: String,

    @Column("open")
    val open: BigDecimal,

    @Column("high")
    val high: BigDecimal,

    @Column("low")
    val low: BigDecimal,

    @Column("close")
    val close: BigDecimal,

    @Column("volume")
    val volume: BigDecimal,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)
