package why_mango.jobs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.*
import why_mango.enums.Currency
import why_mango.enums.Exchange
import why_mango.jobs.dto.OhlcvDayJobRequest
import why_mango.ohlcv.OhlcvDayCreate
import why_mango.ohlcv.OhlcvDayModel
import why_mango.ohlcv.OhlcvDayService
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.CandleDayQuary
import why_mango.upbit.dto.CandleDayResponse
import why_mango.utils.between

@RestController
@RequestMapping("/jobs")
class JobController(
    private val ohlcvDayService: OhlcvDayService,
    private val upbitRest: UpbitRest,
) {

    @PostMapping("/ohlcv-day")
    suspend fun ohlcvDay(@RequestBody body: OhlcvDayJobRequest): Flow<OhlcvDayModel> {
        return upbitRest.getCandleDay(CandleDayQuary(market = "${Currency.KRW}-${body.symbol}"))
            .asFlow()
            .filter { it.candleDateTimeKst.toLocalDate().between(body.startDate, body.endDate) }
            .map { OhlcvDayCreate(
                baseDate = it.candleDateTimeKst.toLocalDate(),
                exchange = Exchange.UPBIT,
                currency = Currency.KRW,
                symbol = body.symbol,
                open = it.openingPrice,
                high = it.highPrice,
                low = it.lowPrice,
                close = it.tradePrice,
                volume = it.candleAccTradeVolume,
            )}
            .map { ohlcvDayService.createOhlcvDay(it) }
    }
}