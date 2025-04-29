package why_mango.strategy.model

enum class TradingState {
    WAITING, // 매매 대기 상태
    PAUSE, // 매매 중지 상태
    HOLDING, // 포지션 보유 상태
    REQUESTED, // 매매 요청 상태
    ERROR, // 에러
}