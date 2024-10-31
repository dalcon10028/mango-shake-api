package why_mango.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import why_mango.candle.CandleServiceFactory
import why_mango.ohlcv.OhlcvDayCreate
import why_mango.ohlcv.OhlcvDayService
import java.time.LocalDate
import why_mango.enums.*
import kotlinx.coroutines.flow.*
import why_mango.ticker_symbol.TickerSymbolService

@Component
class OhlcvScheduler(
    private val ohlcvDayService: OhlcvDayService,
    private val tickerSymbolService: TickerSymbolService,
    private val candleServiceFactory: CandleServiceFactory,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 매일 오전 10시(utc 1시)에 전날 데이터 수집
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    suspend fun ohlcvDay() {
        try {
            logger.info { "Start ohlcvDay" }

            val startDate = LocalDate.now().minusDays(1)
            val endDate = LocalDate.now().minusDays(1)

            tickerSymbolService.getTickerSymbols()
                .onEach { delay(500) } // TODO: upbit api throttling 처리해야함
                .flatMapMerge { tickerSymbol ->
                    candleServiceFactory.get(tickerSymbol.market)
                        .getDayCandles(tickerSymbol.symbol, tickerSymbol.baseCurrency, startDate, endDate)
                        .map { res ->
                            OhlcvDayCreate(
                                baseDate = res.baseDate,
                                exchange = Exchange.UPBIT,
                                currency = Currency.KRW,
                                symbol = tickerSymbol.symbol,
                                open = res.open,
                                high = res.high,
                                low = res.low,
                                close = res.close,
                                volume = res.volume,
                            )
                        }
                }
                .collect { ohlcvDayService.createOhlcvDay(it) }

        } catch (e: Exception) {
            logger.error { e }
        }
    }
}