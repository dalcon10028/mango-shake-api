package why_mango.ohlcv.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.enums.Currency
import why_mango.ohlcv.entity.OhlcvDay
import java.time.LocalDate

interface OhlcvDayRepository : CoroutineCrudRepository<OhlcvDay, Long> {

    suspend fun findByBaseDateAndSymbolAndCurrency(baseDate: LocalDate, symbol: String, currency: Currency): OhlcvDay?
}