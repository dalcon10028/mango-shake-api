package why_mango.jobs

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import why_mango.enums.Currency
import why_mango.enums.Exchange
import why_mango.jobs.dto.OhlcvDayJobDtos
import why_mango.ohlcv.OhlcvDayCreate
import why_mango.ohlcv.OhlcvDayService
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.CandleDayQuary
import why_mango.utils.between
import java.time.LocalDate

@Component
class OhlcvScheduler(
    private val ohlcvDayService: OhlcvDayService,
    private val upbitRest: UpbitRest,
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

            upbitRest.getCandleDay(CandleDayQuary(market = "${Currency.KRW}-${body.symbol}"))
                .asSequence()
                .filter { it.candleDateTimeKst.toLocalDate().between(body.startDate, body.endDate) }
                .map {
                    OhlcvDayCreate(
                        baseDate = it.candleDateTimeKst.toLocalDate(),
                        exchange = Exchange.UPBIT,
                        currency = Currency.KRW,
                        symbol = body.symbol,
                        open = it.openingPrice,
                        high = it.highPrice,
                        low = it.lowPrice,
                        close = it.tradePrice,
                        volume = it.candleAccTradeVolume,
                    )
                }
                .forEach { ohlcvDayService.createOhlcvDay(it) }
        } catch (
            e: Exception
        ) {
            println(e)
        }
    }
}