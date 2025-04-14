package why_mango.strategy.bollinger_bands_trend.model

sealed class TradingState

data object Waiting : TradingState() // 매매 대기 상태

data object Pause : TradingState() // 매매 중지 상태

data object Holding : TradingState() // 포지션 보유 상태

data object Requested : TradingState() // 매매 요청 상태