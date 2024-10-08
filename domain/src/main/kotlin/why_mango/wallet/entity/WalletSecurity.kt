package why_mango.wallet.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import why_mango.enums.Currency
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("wallet_security")
class WalletSecurity (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("wallet_id")
    val walletId: Long,

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
    @Column("last_synced_at")
    var lastSyncedAt: LocalDateTime? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)