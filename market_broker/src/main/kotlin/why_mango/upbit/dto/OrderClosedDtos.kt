package why_mango.upbit.dto

import feign.Param
import kotlinx.serialization.Serializable
import why_mango.serializer.BigDecimalSerializer
import why_mango.serializer.DateTimeSerializer
import why_mango.upbit.enums.OrderType
import why_mango.upbit.enums.Side
import why_mango.upbit.enums.TimeInForce
import java.math.BigDecimal
import java.time.LocalDateTime

// https://docs.upbit.com/reference/%EC%A2%85%EB%A3%8C-%EC%A3%BC%EB%AC%B8-%EC%A1%B0%ED%9A%8C

data class OrderClosedQuery(
    /* 마켓 ID */
    val market: String,

    /* 주문 상태 */
    val state: String? = null,

    /* 주문 상태의 목록 */
    val states: List<String>? = null,

    /* 조회 시작 시간 (주문 생성시간 기준) */
    val start_time: String? = null,

    /* 조회 종료 시간 (주문 생성시간 기준) */
    val end_time: String? = null,

    /* 요청 개수, default: 100, max: 1,000 */
    val limit: String = "1000",

    /* 정렬 방식 */
    @Param("order_by")
    val order_by: String? = null,
)


@Serializable
data class OrderClosedResponse(
    /* 주문의 고유 아이디 */
    val uuid: String,

    /* 주문 종류 */
    val side: Side,

    /* 주문 방식 */
    val ordType: OrderType,

    /* 주문 당시 화폐 가격 */
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal? = null,

    /* 주문 상태 */
    val state: String,

    /* 마켓 ID */
    val market: String,

    /* 주문 생성 시간 */
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,

    /* 사용자가 입력한 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val volume: BigDecimal? = null,

    /* 체결 후 남은 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val remainingVolume: BigDecimal? = null,

    /* 수수료로 예약된 비용 */
    @Serializable(with = BigDecimalSerializer::class)
    val reservedFee: BigDecimal,

    /* 남은 수수료 */
    @Serializable(with = BigDecimalSerializer::class)
    val remainingFee: BigDecimal,

    /* 사용된 수수료 */
    @Serializable(with = BigDecimalSerializer::class)
    val paidFee: BigDecimal,

    /* 거래에 사용 중인 비용 */
    @Serializable(with = BigDecimalSerializer::class)
    val locked: BigDecimal,

    /* 체결된 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val executedVolume: BigDecimal,

    /* 현재까지 체결된 금액 */
    @Serializable(with = BigDecimalSerializer::class)
    val executedFunds: BigDecimal,

    /* 해당 주문에 걸린 체결 수 */
    val tradesCount: Int,

    /* IOC, FOK 설정 */
    val timeInForce: TimeInForce? = null,
)