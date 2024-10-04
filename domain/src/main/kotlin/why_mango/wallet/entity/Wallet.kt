package why_mango.wallet.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import why_mango.wallet.AdditionalInfo
import java.time.LocalDateTime

@Table("wallet")
class Wallet (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("api_provider")
    val apiProvider: String,

    @Column("app_key")
    val appKey: String,

    @Column("app_secret")
    val appSecret: String,

    @Column("additional_info")
    val additionalInfo: AdditionalInfo,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)