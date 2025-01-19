package why_mango.bitget

import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import why_mango.bitget.dto.history_candle_stick.*
import why_mango.utils.*

@Service
class BitgetFutureService (
    private val bitgetRest: BitgetRest,
) {
    suspend fun getHistoryCandlestick(query: HistoryCandlestickQuery): Flow<HistoryCandleStickResponse> {
        return bitgetRest.getHistoryCandlestick(query).data.asFlow()
            .map {
                val (timeStamp, open, high, low, close, volume, amount) = it
                HistoryCandleStickResponse(
                    timeStamp = timeStamp.toLong(),
                    open = open.toBigDecimal(),
                    high = high.toBigDecimal(),
                    low = low.toBigDecimal(),
                    close = close.toBigDecimal(),
                    volume = volume.toBigDecimal(),
                    amount = amount.toBigDecimal()
                )
            }
    }
}