package why_mango.bitget.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import why_mango.bitget.dto.websocket.SubscribeChannel
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import why_mango.bitget.enums.CandleStickChannel
import why_mango.bitget.enums.Granularity
import why_mango.bitget.enums.ProductType
import why_mango.bitget.enums.WebsocketChannel
import java.util.ArrayDeque

class CandleQueue(
    private val symbol: String,
    private val granularity: Granularity,
    private val maxCandleSize: Int = 200,
    private val initialCandles: List<CandleStickPushEvent> = emptyList()
) {
    private val candles = ArrayDeque<CandleStickPushEvent>(maxCandleSize)
    private val _candleFlow = MutableStateFlow(initialCandles)
    val candleFlow get() = _candleFlow.asSharedFlow()

    fun add(event: CandleStickPushEvent) {
        if (candles.isNotEmpty() && candles.last().timestamp == event.timestamp) {
            candles.removeLast()
            candles.addLast(event)
        } else {
            candles.addLast(event)
            if (candles.size > maxCandleSize) {
                candles.removeFirst()
            }
        }
        _candleFlow.value = candles.toList()
    }

    fun subscribeMessage() = SubscribeChannel(
        instType = ProductType.USDT_FUTURES,
        channel = "candle${granularity.value}",
        instId = symbol
    )

}