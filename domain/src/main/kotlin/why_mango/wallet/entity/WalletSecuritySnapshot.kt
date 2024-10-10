package why_mango.wallet.entity

import why_mango.enums.Currency
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*
import java.time.LocalDate

@Table("wallet_security_snapshot")
class WalletSecuritySnapshot (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("wallet_snapshot_id")
    val walletSnapshotId: Long,

    @Column("wallet_id")
    val walletId: Long,

    @Column("wallet_security_id")
    val walletSecurityId: Long,

    @Column("base_date")
    val baseDate: LocalDate,

    @Column("currency")
    val currency: Currency,

    @Column("symbol")
    val symbol: String,

    @Column("balance")
    var balance: BigDecimal,

    @Column("locked")
    var locked: BigDecimal,

    @Column("average_buy_price")
    var averageBuyPrice: BigDecimal,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)