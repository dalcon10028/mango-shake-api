package why_mango.order.model

import why_mango.order.entity.OrderStatus
import why_mango.upbit.dto.OrderClosedResponse
import why_mango.upbit.dto.OrderOpenResponse
import why_mango.upbit.dto.OrderStatusResponse
import why_mango.upbit.enums.*
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderStatusSave(
    val walletId: Long,
    val uuid: String,
    val side: Side,
    val orderType: OrderType,
    val price: BigDecimal?,
    val status: String,
    val market: String,
    val orderedAt: LocalDateTime,
    val volume: BigDecimal?,
    val remainingVolume: BigDecimal?,
    val reservedFee: BigDecimal,
    val remainingFee: BigDecimal,
    val paidFee: BigDecimal,
    val locked: BigDecimal,
    val executedVolume: BigDecimal,
    val executedAmount: BigDecimal,
    val tradesCount: Int,
    val timeInForce: TimeInForce?,
) {
    companion object {
        fun from(response: OrderClosedResponse, walletId: Long) = OrderStatusSave(
            walletId = walletId,
            uuid = response.uuid,
            side = response.side,
            orderType = response.ordType,
            price = response.price,
            status = response.state,
            market = response.market,
            orderedAt = response.createdAt,
            volume = response.volume,
            remainingVolume = response.remainingVolume,
            reservedFee = response.reservedFee,
            remainingFee = response.remainingFee,
            paidFee = response.paidFee,
            locked = response.locked,
            executedVolume = response.executedVolume,
            executedAmount = response.executedVolume,
            tradesCount = response.tradesCount,
            timeInForce = response.timeInForce,
        )

        fun from(response: OrderOpenResponse, walletId: Long) = OrderStatusSave(
            walletId = walletId,
            uuid = response.uuid,
            side = response.side,
            orderType = response.ordType,
            price = response.price,
            status = response.state,
            market = response.market,
            orderedAt = response.createdAt,
            volume = response.volume,
            remainingVolume = response.remainingVolume,
            reservedFee = response.reservedFee,
            remainingFee = response.remainingFee,
            paidFee = response.paidFee,
            locked = response.locked,
            executedVolume = response.executedVolume,
            executedAmount = response.executedFunds,
            tradesCount = response.tradesCount,
            timeInForce = response.timeInForce,
        )
    }

    fun toEntity() = OrderStatus(
        walletId = walletId,
        uuid = uuid,
        side = side,
        orderType = orderType,
        price = price,
        status = status,
        market = market,
        orderedAt = orderedAt,
        volume = volume,
        remainingVolume = remainingVolume,
        reservedFee = reservedFee,
        remainingFee = remainingFee,
        paidFee = paidFee,
        locked = locked,
        executedVolume = executedVolume,
        executedAmount = executedAmount,
        tradesCount = tradesCount,
        timeInForce = timeInForce,
    )
}
