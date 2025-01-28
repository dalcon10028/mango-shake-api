package why_mango.strategy.bollinger_band

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetDemoWebSocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import why_mango.strategy.*
import kotlinx.coroutines.*

@Service
class BollingerBandStateMachine(
    realtimeClient: BitgetDemoWebSocketClient,
) : StrategyStateMachine {
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override var state: TradeState = Waiting

    @OptIn(FlowPreview::class)
    private val priceFlow = realtimeClient.priceEventFlow
        .distinctUntilChanged()
        .sample(1000)

    @OptIn(FlowPreview::class)
    private val candlestickFlow = realtimeClient.candlestickEventFlow
        .distinctUntilChanged()
        .sample(1000)

    fun subscribeEventFlow() = scope.launch {
        priceFlow.combine(candlestickFlow) { price, candlestick ->
            logger.info { "price: $price, candlestick: $candlestick" }
            handle(Tick(price, candlestick))
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