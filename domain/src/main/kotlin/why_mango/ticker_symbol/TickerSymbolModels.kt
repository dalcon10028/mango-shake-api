package why_mango.ticker_symbol

import kotlinx.serialization.Serializable
import why_mango.enums.*
import java.time.LocalDateTime

@Serializable
data class TickerSymbolCreate(
    val symbol: String,
    val baseCurrency: Currency,
    val name: String,
    val market: AssetType,
)

data class TickerSymbolModel(
    val id: Long,
    val symbol: String,
    val name: String,
    val baseCurrency: Currency,
    val market: AssetType,
    val createdAt: LocalDateTime,
)
