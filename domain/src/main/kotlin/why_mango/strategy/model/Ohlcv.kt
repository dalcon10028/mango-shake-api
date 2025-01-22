package why_mango.strategy.model

import why_mango.bitget.dto.market.HistoryCandleStickResponse
import java.math.BigDecimal

data class Ohlcv(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val timeStamp: Long,
) {
    companion object {
        fun from(response: HistoryCandleStickResponse) = Ohlcv(
            open = response.open,
            high = response.high,
            low = response.low,
            close = response.close,
            volume = response.volume,
            timeStamp = response.timeStamp,
        )
    }
}
