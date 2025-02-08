package why_mango.strategy.machines

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
import java.math.RoundingMode

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
                                presetStopSurplusPrice = (it.price * 1.10.toBigDecimal()).setScale(3, RoundingMode.FLOOR),
                                presetStopLossPrice = (it.price * 0.85.toBigDecimal()).setScale(3, RoundingMode.FLOOR)
                            )
                            state = Holding
                            publisher.publishEvent(
                                SlackEvent(
                                    topic = Topic.TRADER,
                                    title = "Request open long position",
                                    color = Color.GOOD,
                                    fields = listOf(
                                        Field("price", it.price),
                                        Field("emaCross", it.emaCross),
                                        Field("jmaSlope", it.jmaSlope),
                                        Field("macdCross", it.macdCross)
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
                                presetStopSurplusPrice = (it.price * 0.85.toBigDecimal()).setScale(3, RoundingMode.FLOOR),
                                presetStopLossPrice = (it.price * 1.10.toBigDecimal()).setScale(3, RoundingMode.FLOOR)
                            )
                            state = Holding
                            publisher.publishEvent(
                                SlackEvent(
                                    topic = Topic.TRADER,
                                    title = "Request open short position",
                                    color = Color.DANGER,
                                    fields = listOf(
                                        Field("price", it.price),
                                        Field("emaCross", it.emaCross),
                                        Field("jmaSlope", it.jmaSlope),
                                        Field("macdCross", it.macdCross)
                                    )
                                )
                            )
                        }
                    }
                }
                .catch { e ->
                    logger.error(e) { "error" }
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.ERROR,
                            title = "Error",
                            color = Color.DANGER,
                            fields = listOf(
                                Field("error", e.message ?: "unknown")
                            )
                        )
                    )
                }
                .collect()
        }

        scope.launch {
            positionFlow
                .onEach { logger.info { "position: $it" } }
                .filter { state == Holding }
                .onEach {
                    state = Waiting
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.TRADER,
                            title = "Position closed",
                            color = if (it.achievedProfits > BigDecimal.ZERO) Color.GOOD else Color.DANGER,
                            fields = listOf(
                                Field("posId", it.posId),
                                Field("Realized PnL", it.achievedProfits),
                                Field("holdSide", it.holdSide),
                                Field("openPriceAvg", it.openPriceAvg),
                                Field("openFee", it.openFee),
                                Field("closeFee", it.closeFee),
                            )
                        )
                    )
                }
                .collect()
        }
    }

    suspend fun closeAll() {
        logger.info { "🧽 close all positions" }
        bitgetFutureService.flashClose("SXRPSUSDT")
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