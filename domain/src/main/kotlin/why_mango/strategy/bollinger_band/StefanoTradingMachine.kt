package why_mango.strategy.bollinger_band

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicDemoWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import why_mango.strategy.*
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.component.slack.Color
import why_mango.component.slack.Field
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic
import why_mango.strategy.enums.CrossResult
import why_mango.strategy.enums.CrossResult.*
import why_mango.strategy.indicator.*
import why_mango.utils.groupBy
import why_mango.utils.windowed
import java.math.BigDecimal

/**
 * 스테파노 매매법
 */
@Service
class StefanoTradingMachine(
    private val publicRealtimeClient: BitgetPublicDemoWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetFutureService: BitgetFutureService,
    private val publisher: ApplicationEventPublisher,
) : StrategyStateMachine {
    companion object {
        private const val BALANCE_USD = 1000
    }

    private val logger = KotlinLogging.logger {}

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override var state: TradeState = Waiting

    private val priceFlow = publicRealtimeClient.priceEventFlow
        .map { it.lastPr }
        .distinctUntilChanged()

    private suspend fun candleSticksFlow() = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }

    private suspend fun emaCrossIndicator(short: Int, long: Int, window: Int) = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(200)
        .map { candles -> candles.emaCross(short, long, window) }
        .map { it.last() }


    private suspend fun macdCrossIndicator() = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(200)
        .map { window -> window.macdCross() }
        .map { it.last() }

    private suspend fun jmaSlopeFlow() = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(200)
        .map { window -> window.jmaSlope() }

    private suspend fun candleSticks() = publicRealtimeClient.candlestickEventFlow
        .groupBy { it.timestamp }
        .onEach {
            logger.info {
                "candleSticks: ${
                    it.second.onEach {
                        it.close
                    }
                }"
            }
        }
        .map { (_, candles) -> candles.last() }
        .map { it.close }

    private val positionFlow = privateRealtimeClient.historyPositionEventFlow

    @OptIn(FlowPreview::class)
    fun subscribeEventFlow() {
        scope.launch {
            combine(
                emaCrossIndicator(12, 26, 5),
                jmaSlopeFlow(),
                macdCrossIndicator(),
                candleSticksFlow(),
                priceFlow,
            ) { emaCross, jmaSlope, macd, candle, price ->
                require(jmaSlope != null) { "jmaSlope is null" }
                StefanoTrade(emaCross, jmaSlope, macd, price)
            }
                .sample(1000)
                .onEach { logger.debug { "StefanoTrade: $it" } }
                .filter { state == Waiting }
                .onEach {
                    when {
                        it.emaCross == GOLDEN_CROSS && it.jmaSlope > BigDecimal.ZERO -> {
                            logger.info { "openLong: $it" }
                            bitgetFutureService.openLong(
                                "SXRPSUSDT",
                                size = (BALANCE_USD.toBigDecimal() / it.price).setScale(0),
                                price = it.price,
                                presetStopSurplusPrice = it.price * 1.15.toBigDecimal(),
                                presetStopLossPrice = it.price * 0.85.toBigDecimal()
                            )
                            state = Holding
                            publisher.publishEvent(
                                SlackEvent(
                                    topic = Topic.NOTIFICATION,
                                    title = "Request open long position",
                                    color = Color.GOOD,
                                    fields = listOf(
                                        Field("price", it.price.toString()),
                                        Field("emaCross", it.emaCross.toString()),
                                        Field("jmaSlope", it.jmaSlope.toString()),
                                        Field("macdCross", it.macdCross.toString())
                                    )
                                )
                            )
                        }

                        it.emaCross == DEATH_CROSS && it.jmaSlope < BigDecimal.ZERO -> {
                            logger.info { "openShort: $it" }
                            bitgetFutureService.openShort(
                                "SXRPSUSDT",
                                size = (BALANCE_USD.toBigDecimal() / it.price).setScale(0),
                                price = it.price,
                                presetStopSurplusPrice = it.price * 0.85.toBigDecimal(),
                                presetStopLossPrice = it.price * 1.15.toBigDecimal()
                            )
                            state = Holding
                            publisher.publishEvent(
                                SlackEvent(
                                    topic = Topic.NOTIFICATION,
                                    title = "Request open short position",
                                    color = Color.DANGER,
                                    fields = listOf(
                                        Field("price", it.price.toString()),
                                        Field("emaCross", it.emaCross.toString()),
                                        Field("jmaSlope", it.jmaSlope.toString()),
                                        Field("macdCross", it.macdCross.toString())
                                    )
                                )
                            )
                        }
                    }
                }
                .collect()

            positionFlow
                .onEach { logger.info { "positionFlow: $it" } }
                .filter { state == Holding }
                .onEach { state = Waiting }
                .collect()
        }
    }

    suspend fun resetState() {
        state = Waiting
    }

    override suspend fun waiting(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }

    override suspend fun requestingPosition(event: StrategyEvent): TradeState = state

    override suspend fun holding(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }

    data class StefanoTrade(
        val emaCross: CrossResult,
        val jmaSlope: BigDecimal,
        val macdCross: CrossResult,
        val price: BigDecimal,
    )
}