package why_mango.strategy.bear_squirrel.model

import java.math.BigDecimal

sealed class TradingEvent

data object Subscribed : TradingEvent()

data class TickerData(
    val symbol: String,
    val price: BigDecimal,
    val signal: Boolean,
    val stopLoss: BigDecimal,
) : TradingEvent()

data class Failure(
    val error: Throwable,
) : TradingEvent()