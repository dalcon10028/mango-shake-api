package why_mango.strategy.machines

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.springframework.context.ApplicationEventPublisher
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.dto.websocket.push_event.HistoryPositionPushEvent
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.component.slack.Color
import why_mango.component.slack.Field
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic
import why_mango.strategy.enums.CrossResult
import why_mango.strategy.enums.CrossResult.*
import why_mango.strategy.indicator.*
import why_mango.utils.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 스테파노 매매법
 */
@Service
class StefanoTradingMachine(
    private val publicRealtimeClient: BitgetPublicWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetFutureService: BitgetFutureService,
    private val publisher: ApplicationEventPublisher,
) {
    companion object {
        private const val BALANCE_USD = 50
        private const val LEVERAGE = 10
        private const val SYMBOL = "XRPUSDT"
    }

    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//    private var _state: TradeState = Waiting
//    val state get() = _state

//    private val priceFlow = publicRealtimeClient.priceEventFlow
//        .map { it.lastPr }
//        .distinctUntilChanged()
//
//    private suspend fun candleSticksFlow1h() = publicRealtimeClient.candlestickEventFlow1h
//        .map { candles -> candles.map { it.close } }
//        .map { candles -> candles.sma(200) }
//        .filterNotNull()
//
//
//    private suspend fun emaCrossIndicator(short: Int, long: Int, window: Int) = publicRealtimeClient.candlestickEventFlow
//        .map { candles -> candles.map { it.close } }
//        .map { candles -> candles.emaCross(short, long, window) }
//        .map { it.lastOrNull() }
//        .filterNotNull()
//
//    private suspend fun jmaSlopeFlow() = publicRealtimeClient.candlestickEventFlow
//        .map { candles -> candles.map { it.close } }
//        .map { window -> window.jmaSlope() }
//        .filterNotNull()
//
//    @OptIn(FlowPreview::class)
//    fun subscribeEventFlow() {
//        scope.launch {
//            combine(
//                emaCrossIndicator(12, 26, 5),
//                jmaSlopeFlow(),
//                candleSticksFlow1h(),
//                priceFlow,
//            ) { emaCross, jmaSlope, candle1h, price ->
////                logger.info { "📈 emaCross: $emaCross, jmaSlope: $jmaSlope, candle1h: $candle1h, price: $price" }
//                StefanoTradeEvent(emaCross, jmaSlope, candle1h, price)
//            }
//                .sample(1000)
//                .onEach {
//                    _state = when (state) {
//                        Waiting -> waiting(it)
//                        RequestingPosition -> requestingPosition(it)
//                        Holding -> state
//                    }
//                }
//                .catch { e ->
//                    logger.error(e) { "error" }
//                    publisher.publishEvent(
//                        SlackEvent(
//                            topic = Topic.ERROR,
//                            title = "[$SYMBOL] Error",
//                            color = Color.DANGER,
//                            fields = listOf(
//                                Field("error", e.message ?: "unknown")
//                            )
//                        )
//                    )
//                }
//                .collect()
//        }
//
//        scope.launch {
//            privateRealtimeClient.xrpusdtPositionHistoryChannel
//                .onEach { logger.info { "position: $it" } }
//                .onEach {
//                    _state = when (state) {
//                        Holding -> holding(it)
//                        else -> state
//                    }
//                }
//                .collect()
//        }
//    }
//
//    suspend fun closeAll() {
//        logger.info { "🧽 close all positions" }
//        bitgetFutureService.flashClose(SYMBOL)
//    }
//
//    suspend fun waiting(stefano: StefanoTradeEvent): TradeState {
//        return when {
//            stefano.isLong -> {
//                bitgetFutureService.openLong(
//                    SYMBOL,
//                    size = (BALANCE_USD.toBigDecimal() * LEVERAGE.toBigDecimal() / stefano.price).setScale(0),
//                    price = stefano.price,
//                    presetStopSurplusPrice = stefano.price.takeProfitLong(0.15, LEVERAGE).setScale(3, RoundingMode.FLOOR),
//                    presetStopLossPrice = stefano.price.stopLossLong(0.1, LEVERAGE).setScale(3, RoundingMode.FLOOR)
//                )
//                publisher.publishEvent(
//                    SlackEvent(
//                        topic = Topic.TRADER,
//                        title = "[$SYMBOL] Request open long position",
//                        color = Color.GOOD,
//                        fields = listOf(
//                            Field("price", stefano.price),
//                            Field("emaCross", stefano.emaCross),
//                            Field("jmaSlope", stefano.jmaSlope),
//                            Field("candle1h", stefano.candle1h)
//                        )
//                    )
//                )
//                Holding
//            }
//
//            stefano.isShort -> {
//                bitgetFutureService.openShort(
//                    SYMBOL,
//                    size = (BALANCE_USD.toBigDecimal() * LEVERAGE.toBigDecimal() / stefano.price).setScale(0),
//                    price = stefano.price,
//                    presetStopSurplusPrice = stefano.price.takeProfitShort(0.15, LEVERAGE).setScale(3, RoundingMode.FLOOR),
//                    presetStopLossPrice = stefano.price.stopLossShort(0.1, LEVERAGE).setScale(3, RoundingMode.FLOOR)
//                )
//                publisher.publishEvent(
//                    SlackEvent(
//                        topic = Topic.TRADER,
//                        title = "[$SYMBOL] Request open short position",
//                        color = Color.DANGER,
//                        fields = listOf(
//                            Field("price", stefano.price),
//                            Field("emaCross", stefano.emaCross),
//                            Field("jmaSlope", stefano.jmaSlope),
//                            Field("candle1h", stefano.candle1h)
//                        )
//                    )
//                )
//                Holding
//            }
//
//            else -> state
//        }
//    }
//
//    suspend fun requestingPosition(event: StefanoTradeEvent): TradeState = state
//
//    suspend fun holding(event: HistoryPositionPushEvent): TradeState {
//        publisher.publishEvent(
//            SlackEvent(
//                topic = Topic.TRADER,
//                title = "[$SYMBOL] Position closed",
//                color = if (event.achievedProfits > BigDecimal.ZERO) Color.GOOD else Color.DANGER,
//                fields = listOf(
//                    Field("posId", event.posId),
//                    Field("Realized PnL", event.achievedProfits),
//                    Field("holdSide", event.holdSide),
//                    Field("openPriceAvg", event.openPriceAvg),
//                    Field("openFee", event.openFee),
//                    Field("closeFee", event.closeFee),
//                )
//            )
//        )
//        return Waiting
//    }
//
//    data class StefanoTradeEvent(
//        val emaCross: CrossResult,
//        val jmaSlope: BigDecimal,
//        val candle1h: BigDecimal,
//        val price: BigDecimal,
//    ) {
//        val isLong: Boolean get() = emaCross == GOLDEN_CROSS && jmaSlope > BigDecimal.ZERO && price > candle1h
//        val isShort: Boolean get() = emaCross == DEATH_CROSS && jmaSlope < BigDecimal.ZERO && price < candle1h
//    }
}