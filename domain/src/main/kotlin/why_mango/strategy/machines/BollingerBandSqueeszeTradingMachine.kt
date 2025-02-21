package why_mango.strategy.machines

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import why_mango.component.slack.*
import why_mango.strategy.indicator.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import why_mango.bitget.dto.websocket.push_event.HistoryPositionPushEvent
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 볼린저 밴드 스퀴즈 매매 머신
 */
@Service
class BollingerBandSqueeszeTradingMachine(
    private val publicRealtimeClient: BitgetPublicWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetFutureService: BitgetFutureService,
    private val publisher: ApplicationEventPublisher,
) {
    companion object {
        private const val BALANCE_USD = 100
        private const val LEVERAGE = 10
        private const val SYMBOL = "SXRPSUSDT"
    }

    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var _state: TradeState = Waiting
    val state get() = _state

    private val priceFlow = publicRealtimeClient.priceEventFlow
        .map { it.lastPr }
        .distinctUntilChanged()

    private val bbFlow = publicRealtimeClient.candlestickEventFlow15m
        .map { candles -> candles.map { it.close } }
        .map { candles -> candles.bollingerBands(20) }
        .map { it.lastOrNull() }
        .filterNotNull()

    private val moneyFlowIndex = publicRealtimeClient.candlestickEventFlow15m
        .map { candles -> candles.moneyFlowIndex() }
        .map { it.lastOrNull() }
        .filterNotNull()

    fun subscribeEventFlow() {
        scope.launch {
            combine(
                priceFlow,
                bbFlow,
                moneyFlowIndex,
                publicRealtimeClient.candlestickEventFlow15m.map { it.last() }.filterNotNull(),
            ) { price, bollingerBand, moneyFlowIndex, candle ->
//                logger.info { "📈 price: $price, bollingerBand: $bollingerBand, width: ${bollingerBand.width}, moneyFlowIndex: $moneyFlowIndex" }
                BollingerBandSqueezeEvent(
                    band = bollingerBand,
                    moneyFlowIndex = moneyFlowIndex,
                    price = price,
                    candle = candle,
                )
            }
                .onEach {
                    _state = when (state) {
                        Waiting -> waiting(it)
                        RequestingPosition -> requestingPosition(it)
                        Holding -> holding(it)
                    }
                }
                .catch { e ->
                    logger.error(e) { "error" }
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.ERROR,
                            title = "[$SYMBOL] Error",
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
            // NOTE: 포지션 종료 이벤트 수신
            privateRealtimeClient.sxrpsusdtPositionHistoryChannel
                .onEach { logger.info { "position: $it" } }
                .onEach { event ->
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.TRADER,
                            title = "[$SYMBOL] Position closed",
                            color = if (event.achievedProfits > BigDecimal.ZERO) Color.GOOD else Color.DANGER,
                            fields = listOf(
                                Field("posId", event.posId),
                                Field("Realized PnL", event.achievedProfits),
                                Field("holdSide", event.holdSide),
                                Field("openPriceAvg", event.openPriceAvg),
                                Field("openFee", event.openFee),
                                Field("closeFee", event.closeFee),
                            )
                        )
                    )
                }
                .collect()
        }
    }

    suspend fun closeAll() {
        logger.info { "🧽 close all positions" }
        bitgetFutureService.flashClose(SYMBOL)
    }

    suspend fun waiting(event: BollingerBandSqueezeEvent): TradeState {
        return when {
            event.isLong -> {
                bitgetFutureService.openLong(
                    SYMBOL,
                    size = (BALANCE_USD.toBigDecimal() * LEVERAGE.toBigDecimal() / event.price).setScale(0),
                    price = event.price,
                    presetStopLossPrice = event.candle.low
                )
                publisher.publishEvent(
                    SlackEvent(
                        topic = Topic.TRADER,
                        title = "[$SYMBOL] Request open long position",
                        color = Color.GOOD,
                        fields = listOf(
                            Field("price", event.price),
                            Field("band", event.band),
                            Field("moneyFlowIndex", event.moneyFlowIndex),
                            Field("candle1h", event.candle)
                        )
                    )
                )
                Holding
            }

            event.isShort -> {
                bitgetFutureService.openShort(
                    SYMBOL,
                    size = (BALANCE_USD.toBigDecimal() * LEVERAGE.toBigDecimal() / event.price).setScale(0),
                    price = event.price,
                    presetStopLossPrice = event.candle.high
                )
                publisher.publishEvent(
                    SlackEvent(
                        topic = Topic.TRADER,
                        title = "[$SYMBOL] Request open short position",
                        color = Color.DANGER,
                        fields = listOf(
                            Field("price", event.price),
                            Field("band", event.band),
                            Field("moneyFlowIndex", event.moneyFlowIndex),
                            Field("candle1h", event.candle)
                        )
                    )
                )
                Holding
            }

            else -> state
        }
    }

    suspend fun requestingPosition(event: BollingerBandSqueezeEvent): TradeState = state

    suspend fun holding(event: BollingerBandSqueezeEvent): TradeState {
        // 이동평균선에 가격이 닿으면 포지션 종료
        if (event.candle.low < event.band.sma && event.candle.high > event.band.sma) {
            logger.info { "🧽 close position" }
            bitgetFutureService.flashClose(SYMBOL)
            return Waiting
        }

        return state
    }

    data class BollingerBandSqueezeEvent(
        val band: BollingerBand,
        val moneyFlowIndex: BigDecimal,
        val price: BigDecimal,
        val candle: CandleStickPushEvent,
    ) {
        val isLong: Boolean get() = band.upper > price && moneyFlowIndex > "80".toBigDecimal()
        val isShort: Boolean get() = band.lower < price && moneyFlowIndex < "20".toBigDecimal()
    }
}