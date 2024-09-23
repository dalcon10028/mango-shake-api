package why_mango.ticker_symbol.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.ticker_symbol.entity.TickerSymbol

interface TickerSymbolRepository : CoroutineCrudRepository<TickerSymbol, Long>