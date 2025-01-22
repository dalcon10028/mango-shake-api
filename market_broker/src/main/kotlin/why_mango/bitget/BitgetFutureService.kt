package why_mango.bitget

import org.springframework.stereotype.Service
import why_mango.bitget.dto.market.*
import why_mango.bitget.enums.ProductType
import why_mango.utils.*

@Service
class BitgetFutureService(
    private val bitgetRest: BitgetRest,
) {
    suspend fun getTicker(symbol: String): TickerResponse {
        val (price) = bitgetRest.getTicker(
            TickerQuery(
                symbol = symbol,
                productType = ProductType.SUSDT_FUTURES
            )
        ).data
        return price
    }

    suspend fun getHistoryCandlestick(query: HistoryCandlestickQuery): Sequence<HistoryCandleStickResponse> {
        return bitgetRest.getHistoryCandlestick(query).data
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
            }.asSequence()
    }
}