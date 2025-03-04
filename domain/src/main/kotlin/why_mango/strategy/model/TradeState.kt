package why_mango.strategy.model

sealed class TradeState

data object Waiting : TradeState() // 매매 대기 상태
data object Pause : TradeState() // 매매 중지 상태
data object Holding : TradeState() // 매수 포지션 보유 상태