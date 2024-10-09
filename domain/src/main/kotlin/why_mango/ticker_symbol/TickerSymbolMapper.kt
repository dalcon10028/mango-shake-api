package why_mango.ticker_symbol

import why_mango.ticker_symbol.entity.TickerSymbol

fun TickerSymbol.toModel(): TickerSymbolModel {
    assert(this.id != null)
    assert(this.createdAt != null)
    return TickerSymbolModel(
        id = this.id!!,
        symbol = this.symbol,
        baseCurrency = this.baseCurrency,
        name = this.name,
        market = this.market,
        createdAt = this.createdAt!!
    )
}

fun TickerSymbolCreate.toEntity(): TickerSymbol {
    return TickerSymbol(
        symbol = this.symbol,
        baseCurrency = this.baseCurrency,
        name = this.name,
        market = this.market
    )
}