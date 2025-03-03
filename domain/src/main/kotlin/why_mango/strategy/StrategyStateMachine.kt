package why_mango.strategy

import why_mango.strategy.model.*

interface StrategyStateMachine {
    var state: TradeState

    suspend fun handle(event: StrategyEvent) {
        state = when (state) {
            is Waiting -> waiting(event)
            is Pause -> pause(event)
            is Holding -> holding(event)
        }
    }

    suspend fun waiting(event: StrategyEvent): TradeState

    suspend fun pause(event: StrategyEvent): TradeState

    suspend fun holding(event: StrategyEvent): TradeState
}