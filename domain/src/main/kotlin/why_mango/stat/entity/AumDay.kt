package why_mango.stat.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import why_mango.enums.*
import java.time.*

@Table("aum_day")
class AumDay(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("base_date")
    val baseDate: LocalDate,

    @Column("currency")
    val currency: Currency,

    @Column("asset_type")
    val assetType: AssetType,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)
