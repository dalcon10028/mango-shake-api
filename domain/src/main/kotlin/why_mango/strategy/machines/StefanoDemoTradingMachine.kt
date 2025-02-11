package why_mango.strategy.machines

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.bitget.dto.websocket.push_event.HistoryPositionPushEvent
import why_mango.bitget.rest.BitgetDemoFutureService
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
//@Service
//class StefanoDemoTradingMachine(
//    private val publicRealtimeClient: BitgetPublicWebsocketClient,
//    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
//    private val bitgetFutureService: BitgetDemoFutureService,
//    private val publisher: ApplicationEventPublisher,
//) {
//    companion object {
//        private const val BALANCE_USD = 1000
//        private const val SYMBOL = "SXRPSUSDT"
//    }
//
//    private val logger = KotlinLogging.logger {}
//    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//    private var _state: TradeState = Waiting
//    val state get() = _state
//
//    private val priceFlow = publicRealtimeClient.priceEventFlow
//        .map { it.lastPr }
//        .distinctUntilChanged()
//
//    private suspend fun candleSticksFlow4h() = publicRealtimeClient.candlestickEventFlow1h
//        .distinctUntilChangedBy { it.timestamp }
//        .map { it.close }
//        .windowed(200)
//        .map { candles -> candles.sma(200) }
//
//    private suspend fun emaCrossIndicator(short: Int, long: Int, window: Int) = publicRealtimeClient.candlestickEventFlow
//        .distinctUntilChangedBy { it.timestamp }
//        .map { it.close }
//        .windowed(200)
//        .map { candles -> candles.emaCross(short, long, window) }
//        .map { it.last() }
//
//
//    private suspend fun macdCrossIndicator() = publicRealtimeClient.candlestickEventFlow
//        .distinctUntilChangedBy { it.timestamp }
//        .map { it.close }
//        .windowed(200)
//        .map { window -> window.macdCross() }
//        .map { it.last() }
//
//    private suspend fun jmaSlopeFlow() = publicRealtimeClient.candlestickEventFlow
//        .distinctUntilChangedBy { it.timestamp }
//        .map { it.close }
//        .windowed(200)
//        .map { window -> window.jmaSlope() }
//
//    private suspend fun candleSticks() = publicRealtimeClient.candlestickEventFlow
//        .groupBy { it.timestamp }
//        .onEach {
//            logger.info {
//                "candleSticks: ${
//                    it.second.onEach {
//                        it.close
//                    }
//                }"
//            }
//        }
//        .map { (_, candles) -> candles.last() }
//        .map { it.close }
//
//    @OptIn(FlowPreview::class)
//    fun subscribeEventFlow() {
//        scope.launch {
//            combine(
//                emaCrossIndicator(12, 26, 5),
//                jmaSlopeFlow(),
//                candleSticksFlow4h(),
//                priceFlow,
//            ) { emaCross, jmaSlope, candle4h, price ->
//                require(jmaSlope != null) { "jmaSlope is null" }
//                StefanoTradeEvent(emaCross, jmaSlope, candle4h, price)
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
//                            title = "Error",
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
//            privateRealtimeClient.sxrpsusdtPositionHistoryChannel
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
//                    size = (BALANCE_USD.toBigDecimal() / stefano.price).setScale(0),
//                    price = stefano.price,
//                    presetStopSurplusPrice = stefano.price.takeProfitLong(0.15, 10).setScale(3, RoundingMode.FLOOR),
//                    presetStopLossPrice = stefano.price.stopLossLong(0.10, 10).setScale(3, RoundingMode.FLOOR)
//                )
//                publisher.publishEvent(
//                    SlackEvent(
//                        topic = Topic.TRADER,
//                        title = "Request open long position",
//                        color = Color.GOOD,
//                        fields = listOf(
//                            Field("price", stefano.price),
//                            Field("emaCross", stefano.emaCross),
//                            Field("jmaSlope", stefano.jmaSlope),
//                            Field("candle4h", stefano.candle4h)
//                        )
//                    )
//                )
//                Holding
//            }
//
//            stefano.isShort -> {
//                bitgetFutureService.openShort(
//                    SYMBOL,
//                    size = (BALANCE_USD.toBigDecimal() / stefano.price).setScale(0),
//                    price = stefano.price,
//                    presetStopSurplusPrice = stefano.price.takeProfitShort(0.15, 10).setScale(3, RoundingMode.FLOOR),
//                    presetStopLossPrice = stefano.price.stopLossShort(0.10, 10).setScale(3, RoundingMode.FLOOR)
//                )
//                publisher.publishEvent(
//                    SlackEvent(
//                        topic = Topic.TRADER,
//                        title = "Request open short position",
//                        color = Color.DANGER,
//                        fields = listOf(
//                            Field("price", stefano.price),
//                            Field("emaCross", stefano.emaCross),
//                            Field("jmaSlope", stefano.jmaSlope),
//                            Field("macdCross", stefano.candle4h)
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
//                title = "Position closed",
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
//        val candle4h: BigDecimal,
//        val price: BigDecimal,
//    ) {
//        val isLong: Boolean get() = emaCross == GOLDEN_CROSS && jmaSlope > BigDecimal.ZERO && price > candle4h
//        val isShort: Boolean get() = emaCross == DEATH_CROSS && jmaSlope < BigDecimal.ZERO && price < candle4h
//    }
//}