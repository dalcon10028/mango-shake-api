package why_mango.wallet.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import why_mango.enums.ApiProvider
import why_mango.wallet.AdditionalInfo
import why_mango.wallet.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("wallet")
class Wallet (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("uid")
    val uid: Long,

    @Column("api_provider")
    val apiProvider: ApiProvider,

    @Column("status")
    val status: Status = Status.ACTIVE,

    @Column("app_key")
    val appKey: String,

    @Column("app_secret")
    val appSecret: String,

    @Column("additional_info")
    val additionalInfo: AdditionalInfo,

    @Column("memo")
    val memo: String? = null,

    @Column("beginning_assets")
    var beginningAssets: BigDecimal = BigDecimal.ZERO,

    @Column("ending_assets")
    var endingAssets: BigDecimal = BigDecimal.ZERO,

    @Column("deposits_during_period")
    var depositsDuringPeriod: BigDecimal = BigDecimal.ZERO,

    @Column("withdrawals_during_period")
    var withdrawalsDuringPeriod: BigDecimal = BigDecimal.ZERO,

    @CreatedDate
    @Column("last_synced_at")
    var lastSyncedAt: LocalDateTime? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)