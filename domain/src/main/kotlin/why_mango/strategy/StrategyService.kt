package why_mango.strategy

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.dto.market.*
import why_mango.bitget.enums.Granularity
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
    private val bitgetFutureService: BitgetFutureService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private var state: StrategyState = StrategyState.WAITING
    private val symbol = "SXRPSUSDT"

    suspend fun next(time: LocalDateTime) {
        // 홀딩중이면 스킵
        if (state == StrategyState.HOLDING) return

        val bollingerBand = getCandles(Granularity.THREE_MINUTES).map { it.close }.toList().bollingerBand()
        val price = getPrice(symbol)

        // 시그널체크
        if (longSignal(price, bollingerBand)) {
            state = open("long", price, bollingerBand)
        } else if (shortSignal(price, bollingerBand)) {
            state = open("short", price, bollingerBand)
        }
    }

    fun longSignal(price: BigDecimal, band: BollingerBand): Boolean {
        val (upper, sma, lower) = band
        return price < lower
    }

    fun shortSignal(price: BigDecimal, band: BollingerBand): Boolean {
        val (upper, sma, lower) = band
        return price > upper
    }

    fun open(position: String, price: BigDecimal, bands: BollingerBand): StrategyState {
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Open $position position",
                color = Color.GOOD,
                fields = listOf(
                    Field("price", price.toString()),
                    Field("sma", bands.sma.toString()),
                    Field("upper", bands.upper.toString()),
                    Field("lower", bands.lower.toString())
                )
            ))
        return StrategyState.HOLDING
    }

    suspend fun close(): StrategyState {
        if (state == StrategyState.WAITING) return StrategyState.WAITING
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Close position",
                color = Color.GOOD,
                fields = listOf()
            ))
        state = StrategyState.WAITING
        return StrategyState.WAITING
    }

    private suspend fun getCandles(granularity: Granularity) = bitgetFutureService.getHistoryCandlestick(
        HistoryCandlestickQuery(
            symbol = symbol,
            granularity = granularity.value,
            limit = 20,
        )
    ).map { Ohlcv.from(it) }

    private suspend fun getPrice(symbol: String) = bitgetFutureService.getTicker(symbol).lastPr
}