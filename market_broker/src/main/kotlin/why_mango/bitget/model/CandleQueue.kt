package why_mango.bitget.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import java.util.ArrayDeque

class CandleQueue(
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
}