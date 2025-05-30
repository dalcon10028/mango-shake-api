package why_mango.upbit.dto

import feign.Param
import kotlinx.serialization.Serializable
import why_mango.serialization.kserialization.serializer.BigDecimalSerializer
import why_mango.serialization.kserialization.serializer.DateTimeSerializer
import why_mango.upbit.enums.OrderType
import why_mango.upbit.enums.Side
import why_mango.upbit.enums.TimeInForce
import java.math.BigDecimal
import java.time.LocalDateTime

// https://docs.upbit.com/reference/id%EB%A1%9C-%EC%A3%BC%EB%AC%B8-%EC%A1%B0%ED%9A%8C

data class OrderStatusQuery(
    /* 마켓 ID */
    val market: String,

    /* 주문 UUID의 목록 (최대 100개) */
    val uuids: List<String>? = null,

    /* 주문 identifier의 목록 (최대 100개) */
    val identifiers: List<String>? = null,

    /* 정렬 방식 */
    @Param("order_by")
    val orderBy: String? = null
) {
    init {
        require(uuids != null || identifiers != null) { "uuids 또는 identifiers 중 한 가지 필드는 필수이며, 두 가지 필드를 함께 사용할 수 없습니다." }
    }
}

@Serializable
data class OrderStatusResponse(
    /* 주문의 고유 아이디 */
    val uuid: String,

    /* 주문 종류 */
    val side: Side,

    /* 주문 방식 */
    val ordType: OrderType,

    /* 주문 당시 화폐 가격 */
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,

    /* 주문 상태 */
    val state: String,

    /* 마켓 ID */
    val market: String,

    /* 주문 생성 시간 */
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,

    /* 사용자가 입력한 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val volume: BigDecimal,

    /* 체결 후 남은 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val remainingVolume: BigDecimal,

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