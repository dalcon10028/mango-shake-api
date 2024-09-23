package why_mango.ticker_symbol

import why_mango.ticker_symbol.entity.TickerSymbol

class TickerSymbolMapper {
    companion object {
        fun toModel(tickerSymbol: TickerSymbol): TickerSymbolModel {
            assert(tickerSymbol.id != null)
            return TickerSymbolModel(
                id = tickerSymbol.id!!,
                symbol = tickerSymbol.symbol,
                name = tickerSymbol.name,
                apiProvider = tickerSymbol.apiProvider,
                createdAt = tickerSymbol.createdAt!!
            )
        }
    }
}