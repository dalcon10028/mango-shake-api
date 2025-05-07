package why_mango.strategy.bear_squirrel

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.bitget.websocket.BitgetPublicWebsocketClient
import why_mango.component.slack.Color
import why_mango.component.slack.Field
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic
import why_mango.strategy.model.Position
import why_mango.strategy.model.TradingState
import why_mango.strategy.model.TradingState.*
import why_mango.strategy.bear_squirrel.model.*
import why_mango.strategy.util.orderSize
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Service
class BearSquirrelTradingMachine(
    private val publicRealtimeClient: BitgetPublicWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetFutureService: BitgetFutureService,
    private val publisher: ApplicationEventPublisher,
    private val properties: Properties,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger = KotlinLogging.logger {}
    private var position: Position? = null
    private var _state: TradingState = WAITING
    private val _stateMutex = Mutex()
    val state: TradingState
        get() = _state

    private val priceFlow = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.priceEventFlow.containsKey(symbol)) { "priceFlow not found for $symbol" }
        publicRealtimeClient.priceEventFlow[symbol]!!
            .map { it.lastPr }
            .distinctUntilChanged()
    }

    private val candleFLow = properties.universe.associateWith { symbol ->
        require(publicRealtimeClient.candlestickEventFlow.containsKey("${symbol}_${properties.timePeriod}")) { "candleStickFlow not found for $symbol" }
        publicRealtimeClient.candlestickEventFlow["${symbol}_${properties.timePeriod}"]!!
            .filterNot { it.isEmpty() }
            .filterNotNull()
            .filter { it.size > 10 }
    }

    suspend fun subscribeTicker(symbol: String) {
        combine(priceFlow[symbol]!!, candleFLow[symbol]!!) { price, candles ->
//            logger.info {
//                """
//                     firstCandle: ${candles.dropLast(4).last().close},
//                     secondCandle: ${candles.dropLast(3).last().close},
//                     thirdCandle: ${candles.dropLast(2).last().close},
//                     forthCandle: ${candles.dropLast(1).last().close},
//                     currentCandle: ${candles.last().close}
//                    """.trimIndent()
//            }

            val conditions = listOf(
                candles.isFirstCandleCondition(),
                candles.isSecondCandleCondition(),
                candles.isThirdCandleCondition(),
                candles.isFourthCandleCondition(),
            )

            // 3개 이상 충족
            if (conditions.count { it } >= 3) {
                logger.info { "[$symbol] ${conditions.joinToString(",")}" }
            }

            TickerData(
                symbol = symbol,
                price = price,
                signal = conditions.all { it },
                stopLoss = candles.map { it.high }.takeLast(20).max()
            )
        }.onEach {
            _stateMutex.withLock {
                _state = when (state) {
                    WAITING -> waiting(it)
                    PAUSE -> TODO()
                    HOLDING -> holding(it)
                    REQUESTED -> TODO()
                    ERROR -> TODO()
                }
            }
        }

//            .onEach {

//            }
            .catch { e ->
                logger.error(e) { "error" }
                _state = ERROR
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

    private suspend fun waiting(event: TickerData): TradingState {
        if (position != null) return state
        if (event.signal.not()) return state

        val orderSize = orderSize(
            contractConfig = bitgetFutureService.getContractConfig(event.symbol),
            entryAmount = properties.entryAmount,
            leverage = properties.leverage,
            price = event.price,
        )

        publisher.publishEvent(
            SlackEvent(
                topic = Topic.TRADER,
                title = "[${event.symbol}] Request open short position",
                color = Color.DANGER,
                fields = listOf(
                    Field("price", event.price),
                    Field("size", orderSize),
                    Field("takeProfit", event.price.multiply("0.85".toBigDecimal())),
                    Field("stopLoss", event.stopLoss),
                )
            )
        )

        position = Position(
            symbol = event.symbol,
            side = "short",
            size = orderSize,
            entryPrice = event.price,
            stopLoss = event.stopLoss,
        )

//        bitgetFutureService.openShort(
//            symbol = event.symbol,
//            size = orderSize(event.symbol, event.price),
//            price = event.price,
//            presetStopLossPrice = event.maxOf3Candles
//        )
        return HOLDING
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize() {
        properties.universe.forEach { symbol ->
            scope.launch {
                subscribeTicker(symbol)
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

    private suspend fun holding(event: TickerData): TradingState {
        if (position == null) return state
        if (event.symbol != position?.symbol) return state

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
                    )
                )
            )
        }

        val takeProfitNotify = fun(pnl: BigDecimal) {
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
                    )
                )
            )
        }

        return when {
            position?.side == "short" && position!!.stopLoss < event.price -> {
                val pnl = (position!!.entryPrice - event.price) * position!!.size
                stopLossNotify(pnl)
                position = null
                WAITING
            }

            position?.side == "short" && position!!.takeProfit != null && position!!.takeProfit!! < event.price -> {
                val pnl = (position!!.entryPrice - event.price) * position!!.size
                takeProfitNotify(pnl)
                position = null
                WAITING
            }

            else -> state
        }
    }

    /**
     * 첫 캔들 조건 충족 여부
     *
     * 양봉
     * 현재 로우 기준 최근 50개의 캔들 바디 길이 리스트 정렬
     * 30% ~ 70% 분위수 사이에 있으면 "적당한 크기"로 간주
     */
    private fun List<CandleStickPushEvent>.isFirstCandleCondition(): Boolean {
        // 마지막 4개(신호 뒤따르는 캔들) 제외
        val candles = this.dropLast(4)
        // 직전 캔들이 음봉이면 실패
        if (candles.last().isBear()) return false

        // 마지막 윈도우 결과 반환
        return candles.windowed(size = 50, step = 1) { window ->
            // 1) 각 캔들의 바디 길이(abs)를 리스트로
            val lengths = window.map { (it.close - it.open).abs() }
            // 2) 정렬해서 분위수 인덱스 계산
            val sorted = lengths.sorted()
            val lowIdx = (sorted.size * 0.3).toInt()
            val highIdx = (sorted.size * 0.7).toInt()
            // 3) 이 윈도우의 '현재' 바디 길이
            val current = lengths.last()
            // 4) 분위수 사이에 들어오는지 판별
            current > sorted[lowIdx] && current < sorted[highIdx]
        }.last()
    }

    /**
     * 두 번째 캔들
     *
     * 직전 캔들의 바디를 70% 이상 덮는 음봉
     */
    private fun List<CandleStickPushEvent>.isSecondCandleCondition(): Boolean {
        val candles = this.dropLast(3)
        if (candles.last().isBull()) return false

        val previousCandle = candles[candles.size - 2]
        val currentCandle = candles.last()

        // 덮은 비율 계산
        val coveredRatio = currentCandle.body / previousCandle.body

        return coveredRatio > BigDecimal("0.7")
    }

    /**
     * 세 번째 캔들
     *
     * 도지 & (양봉이면서 아랫꼬리가 길지 않은 경우)
     */
    private fun List<CandleStickPushEvent>.isThirdCandleCondition(): Boolean {
        val candles = this.dropLast(2)
        val candle = candles.last()
        return candle.isDoji() &&
                candle.isBull() &&
                candle.lowerShadow < candle.length * "0.5".toBigDecimal()
    }

    /**
     * 네 번째 캔들
     *
     * 음봉
     */
    private fun List<CandleStickPushEvent>.isFourthCandleCondition(): Boolean =
        this.dropLast(1)
            .last()
            .isBear()
}