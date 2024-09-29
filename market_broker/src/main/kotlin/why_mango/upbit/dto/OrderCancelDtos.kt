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

// https://docs.upbit.com/reference/%EC%A3%BC%EB%AC%B8-%EC%B7%A8%EC%86%8C

/**
 * uuid	취소할 주문의 UUID	String
 * identifier	조회용 사용자 지정값	String
 *
 * uuid 혹은 identifier 둘 중 하나의 값이 반드시 포함되어야 합니다.
 */

data class OrderCancelQuary(
    /* 취소할 주문의 UUID */
    val uuid: String? = null,

    /* 조회용 사용자 지정값 */
    val identifier: String? = null,
) {
    init {
        require(uuid != null || identifier != null) { "uuid 혹은 identifier 둘 중 하나의 값이 반드시 포함되어야 합니다." }
    }
}

/**
 * uuid	주문의 고유 아이디	String
 * side	주문 종류	String
 * ord_type	주문 방식	String
 * price	주문 당시 화폐 가격	NumberString
 * state	주문 상태	String
 * market	마켓의 유일키	String
 * created_at	주문 생성 시간	String
 * volume	사용자가 입력한 주문 양	NumberString
 * remaining_volume	체결 후 남은 주문 양	NumberString
 * reserved_fee	수수료로 예약된 비용	NumberString
 * remaining_fee	남은 수수료	NumberString
 * paid_fee	사용된 수수료	NumberString
 * locked	거래에 사용중인 비용	NumberString
 * executed_volume	체결된 양	NumberString
 * trades_count	해당 주문에 걸린 체결 수	Integer
 */

@Serializable
data class OrderCancelResponse(
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

    /* 거래에 사용중인 비용 */
    @Serializable(with = BigDecimalSerializer::class)
    val locked: BigDecimal,

    /* 체결된 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val executedVolume: BigDecimal,

    /* 해당 주문에 걸린 체결 수 */
    val tradesCount: Int,
)