package why_mango.strategy

import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import why_mango.bitget.enums.Granularity
import why_mango.bitget.product_type.BitgetDemoFutureService
import why_mango.component.slack.Color
import why_mango.component.slack.Field
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic
import why_mango.strategy.indicator.bollingerBand
import why_mango.strategy.model.BollingerBand
import why_mango.strategy.model.Ohlcv
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class StrategyService(
    private val bitgetFutureService: BitgetDemoFutureService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private var state: StrategyState = StrategyState.WAITING
    private val symbol = "SXRPSUSDT"

    suspend fun next(time: LocalDateTime) = withContext(Dispatchers.IO) {
        // 홀딩중이면 스킵
        if (state == StrategyState.HOLDING) return@withContext

        val (bollingerBand3m, bollingerBand15m, bollingerBand1h) = awaitAll(
            async { getCandles(Granularity.THREE_MINUTES).map { it.close }.toList().bollingerBand() },
            async { getCandles(Granularity.FIFTEEN_MINUTES).map { it.close }.toList().bollingerBand() },
            async { getCandles(Granularity.ONE_HOUR).map { it.close }.toList().bollingerBand() },
        )
        val price = getPrice(symbol)

        // 시그널체크
        if (longSignal(price, bollingerBand3m, bollingerBand15m, bollingerBand1h)) {
            state = open("long", price, bollingerBand3m, bollingerBand15m, bollingerBand1h)
        } else if (shortSignal(price, bollingerBand3m, bollingerBand15m, bollingerBand1h)) {
            state = open("short", price, bollingerBand3m, bollingerBand15m, bollingerBand1h)
        }
    }

    fun longSignal(price: BigDecimal, band3m: BollingerBand, band15m: BollingerBand, band1h: BollingerBand): Boolean {
        val (_, _, lower3m) = band3m
        val (_, _, lower15m) = band15m
        val (_, _, lower1h) = band1h
        return price < lower3m && price < lower15m && price < lower1h
    }

    fun shortSignal(price: BigDecimal, band3m: BollingerBand, band15m: BollingerBand, band1h: BollingerBand): Boolean {
        val (_, upper3m, _) = band3m
        val (_, upper15m, _) = band15m
        val (_, upper1h, _) = band1h
        return price > upper3m && price > upper15m && price > upper1h
    }

    fun open(position: String, price: BigDecimal, band3m: BollingerBand, band15m: BollingerBand, band1h: BollingerBand): StrategyState {
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Open $position position",
                color = Color.GOOD,
                fields = listOf(
                    Field("price", price.toString()),
                    Field("3m", band3m.toString()),
                    Field("15m", band15m.toString()),
                    Field("1h", band1h.toString())
                )
            )
        )
        return StrategyState.HOLDING
    }

    suspend fun close(): StrategyState {
        if (state == StrategyState.WAITING) return StrategyState.WAITING
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Close position",
                color = Color.GOOD,
                fields = listOf(
                    Field("price", getPrice(symbol).toString())
                )
            )
        )
        state = StrategyState.WAITING
        return StrategyState.WAITING
    }

    private suspend fun getCandles(granularity: Granularity) =
        bitgetFutureService.getHistoryCandlestick(symbol, granularity, 200).map { Ohlcv.from(it) }

    private suspend fun getPrice(symbol: String) = bitgetFutureService.getTicker(symbol).lastPr
}