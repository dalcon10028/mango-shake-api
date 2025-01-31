package why_mango.strategy.bollinger_band

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import why_mango.bitget.websocket.BitgetPublicDemoWebsocketClient
import why_mango.strategy.model.*
import kotlinx.coroutines.flow.*
import why_mango.strategy.*
import kotlinx.coroutines.*
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient

@Service
class BollingerBandStateMachine(
    private val publicRealtimeClient: BitgetPublicDemoWebsocketClient,
    private val privateRealitimeClient: BitgetPrivateWebsocketClient,
) : StrategyStateMachine {
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override var state: TradeState = Waiting

    @OptIn(FlowPreview::class)
    private val priceFlow = publicRealtimeClient.priceEventFlow
        .distinctUntilChanged()
        .sample(1000)

    @OptIn(FlowPreview::class)
    private val candlestickFlow = publicRealtimeClient.candlestickEventFlow
        .distinctUntilChanged()
        .sample(1000)

    private val positionFlow = privateRealitimeClient.historyPositionEventFlow

    fun subscribeEventFlow() = scope.launch {
        priceFlow.combine(candlestickFlow) { price, candlestick ->
            logger.info { "price: $price, candlestick: $candlestick" }
//            handle(Tick(price, candlestick))
        }.collect()

        privateRealitimeClient.historyPositionEventFlow.collect {
            logger.info { "position: $it" }
        }
    }

    override suspend fun waiting(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }

    override suspend fun requestingPosition(event: StrategyEvent): TradeState = state

    override suspend fun holding(event: StrategyEvent): TradeState {
        TODO("Not yet implemented")
    }
}