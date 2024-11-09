package why_mango.order.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import why_mango.upbit.enums.OrderType
import why_mango.upbit.enums.Side
import java.math.BigDecimal
import java.time.LocalDateTime

@Table
class OrderStatus(
    @Id
    val id: Long? = null,

    @Column("wallet_id")
    val walletId: Long,

    @Column("uuid")
    val uuid: String,

    @Column("side")
    val side: Side,

    @Column("order_type")
    val orderType: OrderType,

    @Column("price")
    val price: BigDecimal,

    @Column("status")
    val status: String,

    @Column("market")
    val market: String,

    @Column("ordered_at")
    val orderedAt: LocalDateTime,

    @Column("volume")
    val volume: BigDecimal,

    @Column("remaining_volume")
    val remainingVolume: BigDecimal,

    @Column("reserved_fee")
    val reservedFee: BigDecimal,

    @Column("remaining_fee")
    val remainingFee: BigDecimal,

    @Column("paid_fee")
    val paidFee: BigDecimal,

    @Column("locked")
    val locked: BigDecimal,

    @Column("executed_volume")
    val executedVolume: BigDecimal,

    @Column("executed_amount")
    val executedAmount: BigDecimal,

    @Column("trades_count")
    val tradesCount: Int,

    @Column("time_in_force")
    val timeInForce: String? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)