package why_mango.candle

import kotlinx.coroutines.flow.Flow
import why_mango.enums.Currency
import why_mango.enums.AssetType
import java.time.LocalDate

interface CandleService {
    val market: AssetType

    suspend fun getDayCandles(symbol: String, baseCurrency: Currency, startDate: LocalDate, endDate: LocalDate): Flow<DayCandleModel>
}