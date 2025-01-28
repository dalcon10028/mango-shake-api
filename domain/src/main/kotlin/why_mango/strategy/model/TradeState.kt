package why_mango.strategy.model

sealed class TradeState

data object Waiting : TradeState() // 매매 대기 상태
data object RequestingPosition : TradeState() // 포지션 요청 상태
data object Holding : TradeState() // 매수 포지션 보유 상태