package why_mango.strategy.bollinger_band

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicDemoWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import why_mango.strategy.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.asFlux
import why_mango.bitget.BitgetRest
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.strategy.indicator.*
import why_mango.utils.toLocalDateTime
import why_mango.utils.windowed

/**
 * 스테파노 매매법
 */
@Service
class StefanoTradingMachine(
    private val publicRealtimeClient: BitgetPublicDemoWebsocketClient,
    private val privateRealtimeClient: BitgetPrivateWebsocketClient,
    private val bitgetRest: BitgetRest,
) : StrategyStateMachine {
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override var state: TradeState = Waiting

    private val priceFlow = publicRealtimeClient.priceEventFlow
        .map { it.lastPr }
        .distinctUntilChanged()

//    @OptIn(FlowPreview::class)
    private val candlestickFlow = publicRealtimeClient.candlestickEventFlow
//        .map { it.close }
//        .distinctUntilChanged()
//        .sample(1000)

    private suspend fun smaFlow(window: Int) = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(window)
        .map { it.sma(window) }

    private suspend fun macdFlow() = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(200)
        .map { window -> window.macd(fastLength = 12, slowLength = 26, signalLength = 9) }

    private suspend fun jmaSlopeFlow() = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChangedBy { it.timestamp }
        .map { it.close }
        .windowed(200)
        .map { window -> window.jmaSlope() }


    private val positionFlow = privateRealtimeClient.historyPositionEventFlow

    fun subscribeEventFlow() = scope.launch {
//        smaFlow(12).collect {
//            logger.info { "sma12: $it" }
//        }

        combine(
            smaFlow(12),
            smaFlow(26),
            jmaSlopeFlow(),
            priceFlow
        ) { sma12, sma26, jma, price ->
            logger.info { "jma: $jma, sma12: $sma12, sma26: $sma26, price: $price" }
        }.collect()
    }

    override suspend fun waiting(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }

    override suspend fun requestingPosition(event: StrategyEvent): TradeState = state

    override suspend fun holding(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }
}