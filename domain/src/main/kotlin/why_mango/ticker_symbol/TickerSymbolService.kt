package why_mango.ticker_symbol

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import why_mango.ticker_symbol.repository.TickerSymbolRepository

@Service
class TickerSymbolService(
    private val tickerSymbolRepository: TickerSymbolRepository
) {
    suspend fun getTickerSymbols(): Flow<TickerSymbolModel> = tickerSymbolRepository.findAll().map { TickerSymbolMapper.toModel(it) }
}