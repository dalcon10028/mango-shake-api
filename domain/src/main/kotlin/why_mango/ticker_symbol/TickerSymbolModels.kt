package why_mango.ticker_symbol

import why_mango.enums.ApiProvider
import java.time.LocalDateTime

data class TickerSymbolModel(
    val id: Long,
    val symbol: String,
    val name: String,
    val apiProvider: ApiProvider,
    val createdAt: LocalDateTime,
)
