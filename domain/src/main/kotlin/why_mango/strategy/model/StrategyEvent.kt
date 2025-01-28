package why_mango.strategy.model

import why_mango.bitget.dto.websocket.*

sealed class StrategyEvent

data class Tick(
    val price: TickerPushEvent,
    val candlestick: CandleStickPushEvent,
) : StrategyEvent()

/**
 * Open/Close orders are created
 */
data object OrderCreated : StrategyEvent()

/**
 * Open/Close orders are filled
 */
data object OrderFilled : StrategyEvent()

/**
 * Orders are canceled
 */
data object OrderCanceled : StrategyEvent()