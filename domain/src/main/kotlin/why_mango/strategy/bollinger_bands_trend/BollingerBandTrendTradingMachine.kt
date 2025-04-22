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
        require(publicRealtimeClient.candlestickEventFlow.containsKey("${symbol}_${properties.timePeriod}")) { "candleStickFlow not found for $symbol" }
        publicRealtimeClient.candlestickEventFlow["${symbol}_${properties.timePeriod}"]!!
            .filterNot { it.isEmpty() }
            .map { candles -> candles.map { it.close } }
            .map { candles -> candles.bollingerBands(20) }
            .map { it.lastOrNull() }
            .filterNotNull()
    }


    private val moneyFlowIndex = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.candlestickEventFlow.containsKey("${symbol}_${properties.timePeriod}")) { "candleStickFlow not found for $symbol" }
        publicRealtimeClient.candlestickEventFlow["${symbol}_${properties.timePeriod}"]!!
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

        @OptIn(ExperimentalCoroutinesApi::class)
        fun subscribeEventFlow() {
            properties.universe.forEach { symbol ->
                scope.launch {
                    runSignalMonitor(symbol)
                }
            }

            scope.launch {
                // NOTE: 포지션 종료 이벤트 수신
                privateRealtimeClient.positionHistoryChannel.values
                    .asFlow()
                    .flattenConcat()
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
                publicRealtimeClient.candlestickEventFlow["${symbol}_${properties.timePeriod}"]!!.filterNot { it.isEmpty() },
            ) { price, bollingerBand, moneyFlowIndex, candles ->
//                logger.info { "📈 [$symbol] price: $price, bollingerBand: $bollingerBand, width: ${bollingerBand.width}, moneyFlowIndex: $moneyFlowIndex" }
                TickerIndicatorEvent(
                    symbol = symbol,
                    band = bollingerBand,
                    moneyFlowIndex = moneyFlowIndex,
                    price = price,
                    maxOf3Candles = candles.takeLast(3).maxOf { it.high },
                    minOf3Candles = candles.takeLast(3).minOf { it.low },
                    lastCandle = candles.takeLast(2).first(),
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

            if (lastSignal != null && lastSignal?.lastCandle?.timestamp == event.lastCandle.timestamp) {
                return state
            }

            return when {
                event.isLong -> {
                    position = Position(
                        symbol = event.symbol,
                        side = "long",
                        size = orderSize(event.symbol, event.price),
                        entryPrice = event.price,
                        stopLossPrice = event.minOf3Candles
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
                                Field("candle15m", event.lastCandle),
                                Field("minOf3Candles", event.minOf3Candles),
                            )
                        )
                    )
                    bitgetFutureService.openLong(
                        symbol = event.symbol,
                        size = orderSize(event.symbol, event.price),
                        price = event.price,
                        presetStopLossPrice = event.minOf3Candles
                    )
                    Holding
                }

                event.isShort -> {
                    position = Position(
                        symbol = event.symbol,
                        side = "short",
                        size = orderSize(event.symbol, event.price),
                        entryPrice = event.price,
                        stopLossPrice = event.maxOf3Candles
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
                                Field("candle15m", event.lastCandle),
                                Field("maxOf3Candles", event.maxOf3Candles),
                            )
                        )
                    )
                    bitgetFutureService.openShort(
                        symbol = event.symbol,
                        size = orderSize(event.symbol, event.price),
                        price = event.price,
                        presetStopLossPrice = event.maxOf3Candles
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
                position?.side == "long" && event.lastCandle.between(event.band.sma) -> {
                    val pnl = (event.price - position!!.entryPrice) * position!!.size
                    notify(pnl)
                    position = null
                    return Waiting
                }

                position?.side == "short" && event.lastCandle.between(event.band.sma) -> {
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
            val sizeMultiplier = contractConfig.sizeMultiplier            // 0.01

            // 자본 = entryAmount * leverage
            val capital = properties.entryAmount.multiply(properties.leverage)

            // 원시 계산: 자본 / 가격 (충분한 소수점 자리로 계산)
            val rawSize = capital.divide(price, price.scale() + 8, RoundingMode.DOWN)

            // sizeMultiplier 의 정수 배수만큼 뽑아내기
            val multiplierCount = rawSize.divideToIntegralValue(sizeMultiplier)

            // 최종 수량: multiplierCount * sizeMultiplier, 소수점 자리(sizeMultiplier.scale()) 유지
            return multiplierCount
                .multiply(sizeMultiplier)
                .setScale(sizeMultiplier.stripTrailingZeros().scale(), RoundingMode.DOWN)
        }

        data class TickerIndicatorEvent(
            val symbol: String,
            val band: BollingerBand,
            val moneyFlowIndex: BigDecimal,
            val price: BigDecimal,
            val minOf3Candles: BigDecimal,
            val maxOf3Candles: BigDecimal,
            val lastCandle: CandleStickPushEvent, // 마지막 완성 캔들
        ) {
            val isLong: Boolean get() = band.upper < price && moneyFlowIndex > "80".toBigDecimal()
            val isShort: Boolean get() = band.lower > price && moneyFlowIndex < "20".toBigDecimal()
        }
    }