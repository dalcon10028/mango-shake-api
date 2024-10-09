package why_mango.ticker_symbol

import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@Tag(name = "Ticker Symbol", description = "Ticker Symbol Api")
@RestController
@RequestMapping("/ticker-symbols")
class TickerSymbolController(
    private val tickerSymbolService: TickerSymbolService
) {

    @GetMapping
    suspend fun getTickerSymbols(): Flow<TickerSymbolModel> = tickerSymbolService.getTickerSymbols()

    @PostMapping
    suspend fun create(@RequestBody create: TickerSymbolCreate) = tickerSymbolService.create(create)

}