package why_mango.candle

import kotlinx.coroutines.flow.Flow
import why_mango.enums.Market
import java.time.LocalDate

interface CandleService {
    val market: Market

    suspend fun getDayCandles(symbol: String, startDate: LocalDate, endDate: LocalDate): Flow<DayCandleModel>
}