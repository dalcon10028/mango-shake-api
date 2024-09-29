package why_mango.upbit.dto

import kotlinx.serialization.Serializable
import why_mango.serializer.BigDecimalSerializer
import java.math.BigDecimal

// https://docs.upbit.com/reference/%EC%A0%84%EC%B2%B4-%EA%B3%84%EC%A2%8C-%EC%A1%B0%ED%9A%8C

@Serializable
data class AccountResponse(
    /* 화폐를 의미하는 영문 대문자 코드 */
    val currency: String,

    /* 주문가능 금액/수량 */
    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal,

    /* 주문 중 묶여있는 금액/수량 */
    @Serializable(with = BigDecimalSerializer::class)
    val locked: BigDecimal,

    /* 매수평균가 */
    @Serializable(with = BigDecimalSerializer::class)
    val avgBuyPrice: BigDecimal,

    /* 매수평균가 수정 여부 */
    val avgBuyPriceModified: Boolean,

    /* 평단가 기준 화폐 */
    val unitCurrency: String
)