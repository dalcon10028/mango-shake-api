package why_mango.wallet.entity

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import why_mango.enums.ApiProvider
import why_mango.wallet.serializer.AdditionalInfoSerializer
import java.time.LocalDateTime

@Table("wallet")
class Wallet (
    @Id
    val id: Long? = null,

    @Column("api_provider")
    val apiProvider: ApiProvider,

    @Column("app_key")
    val appKey: String,

    @Column("app_secret")
    val appSecret: String,

    @Column("additional_info")
    val additionalInfo: AdditionalInfo? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)

// https://stackoverflow.com/questions/66690712/kotlinx-serialization-polymorphic-serializer-was-not-found-for-missing-class-di

@Serializable(with = AdditionalInfoSerializer::class)
sealed class AdditionalInfo {
    abstract val apiProvider: ApiProvider
}

@Serializable
class UpbitAdditionalInfo(
    override val apiProvider: ApiProvider
) : AdditionalInfo()