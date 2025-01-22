package why_mango.bitget

import feign.*
import why_mango.bitget.dto.BitgetResponse
import why_mango.bitget.dto.market.*
import why_mango.bitget.dto.position.*

interface BitgetRest {
    /**
     * Get ticker data of the given 'productType' and 'symbol'
     * Frequency limit: 20 times/1s (IP)
     */
    @RequestLine("GET /api/v2/mix/market/ticker")
    suspend fun getTicker(@QueryMap query: TickerQuery): BitgetResponse<List<TickerResponse>>

    /**
     * Get Historical Candlestick
     */
    @RequestLine("GET /api/v2/mix/market/history-candles")
    suspend fun getHistoryCandlestick(@QueryMap query: HistoryCandlestickQuery): BitgetResponse<List<List<String>>>

    /**
     * Get All Positions
     *
     * Returns information about all current positions with the given productType
     */
    @RequestLine("GET /api/v2/mix/position/all-position")
    suspend fun getAllPositions(@QueryMap query: AllPositionsQuery): BitgetResponse<List<PositionResponse>>

//    /**
//     * Place Order
//     */
//    @RequestLine("POST /api/v2/mix/order/place-order")
//    suspend fun placeOrder(body: PlaceOrderRequest): BitgetResponse<PlaceOrderResponse>
}