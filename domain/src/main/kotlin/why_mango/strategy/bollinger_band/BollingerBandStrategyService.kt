package why_mango.strategy.bollinger_band

import kotlinx.coroutines.*
import why_mango.strategy.model.*
import why_mango.component.slack.*
import why_mango.strategy.enums.StrategyState.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import why_mango.bitget.enums.Granularity
import why_mango.bitget.BitgetFutureService
import why_mango.strategy.enums.StrategyState
import why_mango.strategy.indicator.bollingerBand
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class BollingerBandStrategyService(
    private val bitgetFutureService: BitgetFutureService,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private var state: StrategyState = WAITING
    private val symbol = "SXRPSUSDT"

    companion object {
        private val THREE = 3.toBigDecimal()
        private val DEAL_AMOUNT_USD = "200".toBigDecimal()
    }

    suspend fun next(time: LocalDateTime) = withContext(Dispatchers.IO) {
        // 포지션 요청중이면 무시
        if (state == REQUESTING_POSITION) return@withContext

        val bollingerBand3m = getCandles(Granularity.THREE_MINUTES).map { it.close }.toList().bollingerBand(THREE)
        val price = getPrice(symbol)

        when (state) {
            HOLDING_LONG, HOLDING_SHORT -> {
                if (closeSignal(price, bollingerBand3m)) {
                    state = close()
                }
            }
            WAITING -> {
                if (longSignal(price, bollingerBand3m)) {
                    state = openLong(price, bollingerBand3m)
                } else if (shortSignal(price, bollingerBand3m)) {
                    state = openShort(price, bollingerBand3m)
                }
            }
            else -> {
                // do nothing
            }
        }
    }

    private suspend fun longSignal(price: BigDecimal, band: BollingerBand): Boolean {
        val (_, _, lower) = band
        return price < lower
    }

    private suspend fun shortSignal(price: BigDecimal, band: BollingerBand): Boolean {
        val (upper, _, _) = band
        return price > upper
    }

    private suspend fun closeSignal(price: BigDecimal, band: BollingerBand): Boolean {
        val (_, sma, _) = band
        return when (state) {
            HOLDING_LONG -> {
                price > sma
            }
            HOLDING_SHORT -> {
                price < sma
            }
            else -> false
        }
    }

    suspend fun openLong(price: BigDecimal, band: BollingerBand): StrategyState {
        bitgetFutureService.openLong(
            symbol,
            size = DEAL_AMOUNT_USD.divide(price),
            price,
        )
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Request open long position",
                color = Color.GOOD,
                fields = listOf(
                    Field("price", price.toString()),
                    Field("band3M", band.toString()),
                )
            )
        )
        return REQUESTING_POSITION
    }

    suspend fun openShort(price: BigDecimal, band: BollingerBand): StrategyState {
        bitgetFutureService.openShort(
            symbol,
            size = DEAL_AMOUNT_USD.divide(price),
            price,
        )
        eventPublisher.publishEvent(
            SlackEvent(
                topic = Topic.NOTIFICATION,
                title = "Request open short position",
                color = Color.GOOD,
                fields = listOf(
                    Field("price", price.toString()),
                    Field("band3M", band.toString()),
                )
            )
        )
        return REQUESTING_POSITION
    }

    suspend fun close(): StrategyState {
        when (state) {
            HOLDING_LONG -> bitgetFutureService.closeLong(symbol)
            HOLDING_SHORT -> bitgetFutureService.closeShort(symbol)
            else -> return state
        }
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
        return WAITING
    }

    private suspend fun getCandles(granularity: Granularity) =
        bitgetFutureService.getHistoryCandlestick(symbol, granularity, 200).map { Ohlcv.from(it) }

    private suspend fun getPrice(symbol: String) = bitgetFutureService.getTicker(symbol).lastPr
}