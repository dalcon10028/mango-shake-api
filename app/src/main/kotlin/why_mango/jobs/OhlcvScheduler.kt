package why_mango.jobs

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import why_mango.candle.CandleServiceFactory
import why_mango.jobs.dto.OhlcvDayJobDtos
import why_mango.ohlcv.OhlcvDayCreate
import why_mango.ohlcv.OhlcvDayService
import java.time.LocalDate
import why_mango.enums.*
import kotlinx.coroutines.flow.*

@Component
class OhlcvScheduler(
    private val ohlcvDayService: OhlcvDayService,
    private val candleServiceFactory: CandleServiceFactory,
) {

    /**
     * 매일 오전 10시에 전날 데이터 수집
     */
    @Scheduled(cron = "0 0 10 * * *")
    suspend fun ohlcvDay() {
        try {
            val body = OhlcvDayJobDtos(
                symbol = "SOL",
                startDate = LocalDate.now().minusDays(1),
                endDate = LocalDate.now().minusDays(1),
            )

            candleServiceFactory.get(Market.CRYPTO_CURRENCY)
                .getDayCandles(body.symbol, body.startDate, body.endDate)
                .map {
                    OhlcvDayCreate(
                        baseDate = it.baseDate,
                        exchange = Exchange.UPBIT,
                        currency = Currency.KRW,
                        symbol = body.symbol,
                        open = it.open,
                        high = it.high,
                        low = it.low,
                        close = it.close,
                        volume = it.volume,
                    )
                }
                .collect { ohlcvDayService.createOhlcvDay(it) }
        } catch (
            e: Exception
        ) {
            println(e)
        }
    }
}