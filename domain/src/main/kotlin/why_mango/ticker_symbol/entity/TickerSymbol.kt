package why_mango.ticker_symbol.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import why_mango.enums.*
import java.time.LocalDateTime

@Table("ticker_symbol")
class TickerSymbol (
    @Id
    val id: Long? = null,

    @Column("symbol")
    val symbol: String,

    @Column("base_currency")
    val baseCurrency: Currency,

    @Column("name")
    val name: String,

    @Column("market")
    val market: Market,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)