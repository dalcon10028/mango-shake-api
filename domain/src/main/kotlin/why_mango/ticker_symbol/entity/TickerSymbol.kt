package why_mango.ticker_symbol.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import why_mango.enums.ApiProvider
import java.time.LocalDateTime

@Table("ticker_symbol")
class TickerSymbol (
    @Id
    val id: Long? = null,

    @Column("symbol")
    val symbol: String,

    @Column("name")
    val name: String,

    @Column("api_provider")
    val apiProvider: ApiProvider,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)