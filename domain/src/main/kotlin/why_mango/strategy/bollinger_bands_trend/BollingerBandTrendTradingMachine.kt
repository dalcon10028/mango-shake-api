package why_mango.strategy.bollinger_bands_trend

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import why_mango.component.slack.*
import why_mango.strategy.indicator.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.strategy.bollinger_bands_trend.enums.TradingEvent
import why_mango.strategy.bollinger_bands_trend.enums.TradingEvent.*
import why_mango.strategy.bollinger_bands_trend.model.TradingState
import why_mango.strategy.bollinger_bands_trend.model.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 볼린저 밴드 추세 매매
 */
@Service
class BollingerBandTrendTradingMachine(
    private val publicRealtimeClient: BitgetPublicWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetFutureService: BitgetFutureService,
    private val publisher: ApplicationEventPublisher,
    private val properties: BollingerBandTrendProperties,
) {
    companion object {
        private const val BALANCE_USD = 100
        private const val LEVERAGE = 10
        private const val MINUTE15 = "15m"
    }

    private val logger = KotlinLogging.logger {}

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var _state: TradingState = Waiting
    private var position: Position? = null
    private var lastSignal: TickerIndicatorEvent? = null
    private val stateMutex = Mutex()
    val state get() = _state

    private val priceFlow = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.priceEventFlow.containsKey(symbol)) { "priceFlow not found for $symbol" }
        publicRealtimeClient.priceEventFlow[symbol]!!
            .map { it.lastPr }
            .distinctUntilChanged()
    }

    private val bbFlow = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.candlestickEventFlow.containsKey("${symbol}_$MINUTE15")) { "candleStickFlow not found for $symbol" }
        publicRealtimeClient.candlestickEventFlow["${symbol}_$MINUTE15"]!!
            .filterNot { it.isEmpty() }
            .map { candles -> candles.map { it.close } }
            .map { candles -> candles.bollingerBands(20) }
            .map { it.lastOrNull() }
            .filterNotNull()
    }


    private val moneyFlowIndex = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.candlestickEventFlow.containsKey("${symbol}_$MINUTE15")) { "candleStickFlow not found for $symbol" }
        publicRealtimeClient.candlestickEventFlow["${symbol}_$MINUTE15"]!!
            .filterNot { it.isEmpty() }
            .map { candles -> candles.moneyFlowIndex(24) }
            .map { it.lastOrNull() }
            .filterNotNull()
    }

    suspend fun processEvent(event: TradingEvent, ticker: TickerIndicatorEvent) {
        stateMutex.withLock {
//            _state = when (event) {
//                TICK -> {
//
//                }
//
//                SLACK_COMMAND -> {
//
//                }
//            }
        }
    }

//    suspend fun processTicker(ticker: TickerIndicatorEvent): TradingState {
//        return when (state) {
//            Waiting -> waiting(ticker)
//            Pause -> pause(ticker)
//            Holding -> holding(ticker)
//        }
//    }

        fun subscribeEventFlow() {
            properties.universe.forEach { symbol ->
                scope.launch {
                    runSignalMonitor(symbol)
                }
            }

            scope.launch {
                // NOTE: 포지션 종료 이벤트 수신
                privateRealtimeClient.sxrpsusdtPositionHistoryChannel
                    .onEach { logger.info { "position: $it" } }
                    .onEach { event ->
                        publisher.publishEvent(
                            SlackEvent(
                                topic = Topic.TRADER,
                                title = "[${event.instId}] Position closed",
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

        suspend fun runSignalMonitor(symbol: String) {
            combine(
                priceFlow[symbol]!!,
                bbFlow[symbol]!!,
                moneyFlowIndex[symbol]!!,
                publicRealtimeClient.candlestickEventFlow["${symbol}_$MINUTE15"]!!.filterNot { it.isEmpty() }.map { it.last() }.filterNotNull(),
            ) { price, bollingerBand, moneyFlowIndex, candle ->
//                logger.info { "📈 [$symbol] price: $price, bollingerBand: $bollingerBand, width: ${bollingerBand.width}, moneyFlowIndex: $moneyFlowIndex" }
                TickerIndicatorEvent(
                    symbol = symbol,
                    band = bollingerBand,
                    moneyFlowIndex = moneyFlowIndex,
                    price = price,
                    candle = candle,
                )
            }
                .onEach {
                    stateMutex.withLock {
                        _state = when (state) {
                            Waiting -> waiting(it)
                            Requested -> TODO()
                            Pause -> pause(it)
                            Holding -> holding(it)
                        }
                    }
                }
                .catch { e ->
                    logger.error(e) { "error" }
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.ERROR,
                            title = "[$symbol] Error",
                            color = Color.DANGER,
                            fields = listOf(
                                Field("error", e.message ?: "unknown")
                            )
                        )
                    )
                }
                .collect()
        }

        suspend fun closeAll() {
            logger.info { "🧽 close all positions" }
//        bitgetFutureService.flashClose()
        }

        suspend fun waiting(event: TickerIndicatorEvent): TradingState {
            if (position != null) {
                return state
            }

            if (lastSignal != null && lastSignal?.candle?.timestamp == event.candle.timestamp) {
                return state
            }

            return when {
                event.isLong -> {
                    position = Position(
                        symbol = event.symbol,
                        side = "long",
                        size = orderSize(event.symbol, event.price),
                        entryPrice = event.price,
                        stopLossPrice = event.candle.low
                    )
                    lastSignal = event

                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.TRADER,
                            title = "[${event.symbol}] Request open long position",
                            color = Color.GOOD,
                            fields = listOf(
                                Field("price", event.price),
                                Field("size", orderSize(event.symbol, event.price)),
                                Field("band", event.band),
                                Field("moneyFlowIndex", event.moneyFlowIndex),
                                Field("candle15m", event.candle)
                            )
                        )
                    )
                    bitgetFutureService.openLong(
                        symbol = event.symbol,
                        size = (properties.entryAmount * properties.leverage / event.price).setScale(0),
                        price = event.price,
                        presetStopLossPrice = event.candle.low
                    )
                    Holding
                }

                event.isShort -> {
                    position = Position(
                        symbol = event.symbol,
                        side = "short",
                        size = orderSize(event.symbol, event.price),
                        entryPrice = event.price,
                        stopLossPrice = event.candle.high
                    )
                    lastSignal = event
                    publisher.publishEvent(
                        SlackEvent(
                            topic = Topic.TRADER,
                            title = "[${event.symbol}] Request open short position",
                            color = Color.DANGER,
                            fields = listOf(
                                Field("price", event.price),
                                Field("size", orderSize(event.symbol, event.price)),
                                Field("band", event.band),
                                Field("moneyFlowIndex", event.moneyFlowIndex),
                                Field("candle15m", event.candle)
                            )
                        )
                    )
                    bitgetFutureService.openShort(
                        symbol = event.symbol,
                        size = (BALANCE_USD.toBigDecimal() * LEVERAGE.toBigDecimal() / event.price).setScale(0),
                        price = event.price,
                        presetStopLossPrice = event.candle.high
                    )
                    Holding
                }

                else -> state
            }
        }

        suspend fun pause(event: TickerIndicatorEvent): TradingState = state

        suspend fun holding(event: TickerIndicatorEvent): TradingState {
            if (position == null || position?.symbol != event.symbol) {
                return state
            }

            val stopLossNotify = fun(pnl: BigDecimal) {
                publisher.publishEvent(
                    SlackEvent(
                        topic = Topic.TRADER,
                        title = "[${event.symbol}] StopLoss close position",
                        color = Color.DANGER,
                        fields = listOf(
                            Field("closePrice", event.price),
                            Field("openPrice", position!!.entryPrice),
                            Field("Realized PnL", pnl),
                            Field("holdSide", position!!.side),
                            Field("sma", event.band.sma),
                        )
                    )
                )
            }

            val notify = fun(pnl: BigDecimal) {
                publisher.publishEvent(
                    SlackEvent(
                        topic = Topic.TRADER,
                        title = "[${event.symbol}] close position",
                        color = if (pnl > BigDecimal.ZERO) Color.GOOD else Color.DANGER,
                        fields = listOf(
                            Field("closePrice", event.price),
                            Field("openPrice", position!!.entryPrice),
                            Field("Realized PnL", pnl),
                            Field("holdSide", position!!.side),
                            Field("sma", event.band.sma),
                        )
                    )
                )
            }

            when {
                position?.side == "long" && position!!.stopLossPrice > event.price -> {
                    val pnl = (event.price - position!!.entryPrice) * position!!.size
                    stopLossNotify(pnl)
                    position = null
                    return Waiting
                }

                position?.side == "short" && position!!.stopLossPrice < event.price -> {
                    val pnl = (position!!.entryPrice - event.price) * position!!.size
                    stopLossNotify(pnl)
                    position = null
                    return Waiting
                }
                // 이동평균선에 가격이 닿으면 포지션 종료
                position?.side == "long" && event.candle.between(event.band.sma) -> {
                    val pnl = (event.price - position!!.entryPrice) * position!!.size
                    notify(pnl)
                    position = null
                    return Waiting
                }

                position?.side == "short" && event.candle.between(event.band.sma) -> {
                    val pnl = (position!!.entryPrice - event.price) * position!!.size
                    notify(pnl)
                    position = null
                    return Waiting
                }

            }

            return state
        }

        // TODO: Cache
        suspend fun orderSize(symbol: String, price: BigDecimal): BigDecimal {
            val contractConfig = bitgetFutureService.getContractConfig(symbol)
            val sizeMultiplier = contractConfig.sizeMultiplier
            val rawSize = BALANCE_USD.toBigDecimal().setScale(10) * LEVERAGE.toBigDecimal() / price

            // rawSize가 sizeMultiplier의 몇 배인지 계산 후, 그 배수에 맞춰 조정
            val multiplierCount = rawSize.divide(sizeMultiplier, 0, RoundingMode.DOWN)
            return multiplierCount.multiply(sizeMultiplier)
                // sizeMultiplier의 소수점 자릿수를 유지하도록 setScale
                .setScale(sizeMultiplier.stripTrailingZeros().scale(), RoundingMode.DOWN)
        }

        data class TickerIndicatorEvent(
            val symbol: String,
            val band: BollingerBand,
            val moneyFlowIndex: BigDecimal,
            val price: BigDecimal,
            val candle: CandleStickPushEvent,
        ) {
            val isLong: Boolean get() = band.upper < price && moneyFlowIndex > "80".toBigDecimal()
            val isShort: Boolean get() = band.lower > price && moneyFlowIndex < "20".toBigDecimal()
        }
    }