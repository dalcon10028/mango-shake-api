package why_mango.wallet.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import java.math.BigDecimal
import java.time.*

@Table("wallet_snapshot")
class WalletSnapshot (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("wallet_id")
    val walletId: Long,

    @Column("base_date")
    val baseDate: LocalDate,

    @Column("beginning_assets")
    var beginningAssets: BigDecimal,

    @Column("ending_assets")
    var endingAssets: BigDecimal,

    @Column("deposits_during_period")
    var depositsDuringPeriod: BigDecimal,

    @Column("withdrawals_during_period")
    var withdrawalsDuringPeriod: BigDecimal,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)