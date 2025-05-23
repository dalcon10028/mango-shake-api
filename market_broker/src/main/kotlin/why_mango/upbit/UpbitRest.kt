package why_mango.upbit

import feign.*
import why_mango.upbit.dto.*

@Headers("Authorization: {token}")
interface UpbitRest {
    /**
     * API 키 리스트 조회
     */
    @RequestLine("GET /api_keys")
    suspend fun getApiKeys(): List<ApiKeyResponse>

    /**
     * 전체 계좌 조회
     */
    @RequestLine("GET /accounts")
    suspend fun getAccounts(@Param token: String): List<AccountResponse>

    /**
     * id로 주문리스트 조회
     */
    @RequestLine("GET /orders/uuids")
    suspend fun getOrders(@Param token: String, @QueryMap query: OrderStatusQuery): List<OrderStatusResponse>

    /**
     * 체결 대기 주문 (Open Order) 조회
     */
    @RequestLine("GET /orders/open")
    suspend fun getOpenOrders(@Param token: String, @QueryMap query: OrderOpenQuery): List<OrderOpenResponse>

    /**
     * 종료된 주문 (Closed Order) 조회
     */
    @RequestLine("GET /orders/closed")
    suspend fun getClosedOrders(
        @Param token: String,
        @QueryMap query: OrderClosedQuery
    ): List<OrderClosedResponse>

    /**
     * 주문하기
     */
    @RequestLine("POST /orders")
    suspend fun order(body: OrderRequestDto): OrderRequestResponse

    /**
     * 주문 취소 접수
     */
    @RequestLine("DELETE /order")
    suspend fun cancelOrder(@QueryMap query: OrderCancelQuary): OrderCancelResponse

    /**
     * 일(Day) 캔들
     */
    @RequestLine("GET /candles/days")
    suspend fun getCandleDay(@Param token: String, @QueryMap query: CandleDayQuery): List<CandleDayResponse>
}