package why_mango.bitget

import feign.*
import why_mango.bitget.dto.*
import why_mango.bitget.dto.account.*
import why_mango.bitget.dto.market.*
import why_mango.bitget.dto.position.*
import why_mango.bitget.dto.trade.*

interface BitgetRest {
    /**
     * Gateway
     */
    @RequestLine("POST /api/v2/mix/gateway")
    suspend fun gatewayPost(@QueryMap params: Map<String, Any>, body: Any): BitgetResponse<Any>

    /**
     * Get account details with the given 'marginCoin' and 'productType'
     * Frequency limit: 10 times/1s (uid)
     */
    @RequestLine("GET /api/v2/mix/account/account")
    suspend fun getAccount(@QueryMap query: AccountQuery): BitgetResponse<AccountResponse>

    /**
     * Get ticker data of the given 'productType' and 'symbol'
     * Frequency limit: 20 times/1s (IP)
     */
    @RequestLine("GET /api/v2/mix/market/ticker")
    suspend fun getTicker(@QueryMap query: TickerQuery): BitgetResponse<List<TickerResponse>>

    /**
     * Get Candlestick Data
     * By default, 100 records are returned. If there is no data, an empty array is returned. The queryable data history varies depending on the k-line granularity.
     */
    @RequestLine("GET /api/v2/mix/market/candles")
    suspend fun getCandlestick(@QueryMap query: CandlestickQuery): BitgetResponse<List<List<String>>>

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

    /**
     * Place Order
     */
    @RequestLine("POST /api/v2/mix/order/place-order")
    suspend fun placeOrder(body: PlaceOrderRequest): BitgetResponse<PlaceOrderResponse>

    /**
     * Flash Close Position
     * Frequency limit: 1 time/1s (User ID)
     * close position at market price
     */
    @RequestLine("POST /api/v2/mix/order/close-positions")
    suspend fun flashClosePosition(body: FlashClosePositionRequest): BitgetResponse<FlashClosePositionResponse>

    /**
     * Interface is used to get future contract details.
     * Rate Limit: 20 req/sec/IP
     */
    @RequestLine("GET /api/v2/mix/market/contracts")
    suspend fun getContractConfig(@QueryMap query: ContractConfigQuery): BitgetResponse<List<ContractConfigResponse>>
}