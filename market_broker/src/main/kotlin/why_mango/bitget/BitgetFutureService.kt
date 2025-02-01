package why_mango.bitget

import why_mango.bitget.dto.market.*
import why_mango.bitget.dto.trade.*
import why_mango.bitget.enums.*
import java.math.BigDecimal


interface BitgetFutureService {
    val productType: ProductType

    suspend fun getTicker(symbol: String): TickerResponse

    suspend fun getCandlestick(
        symbol: String,
        granularity: Granularity,
        limit: Int
    ): List<CandleStickResponse>

    suspend fun getHistoryCandlestick(
        symbol: String,
        granularity: Granularity,
        limit: Int
    ): List<HistoryCandleStickResponse>

    suspend fun openLong(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal? = null,
        orderId: String? = null,
        presetStopSurplusPrice: BigDecimal? = null,
        presetStopLossPrice: BigDecimal? = null
    ): PlaceOrderResponse

    suspend fun openShort(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal? = null,
        orderId: String? = null,
        presetStopSurplusPrice: BigDecimal? = null,
        presetStopLossPrice: BigDecimal? = null
    ): PlaceOrderResponse

    suspend fun closeLong(
        symbol: String,
    ): Boolean

    suspend fun closeShort(
        symbol: String,
    ): Boolean

    suspend fun flashClose(
        symbol: String,
        holdSide: PositionDirection? = null,
    ): Boolean
}