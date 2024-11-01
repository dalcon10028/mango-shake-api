package why_mango.user.entity

import java.time.LocalDateTime
import why_mango.user.enums.*
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.*

@Table("users")
class User (
    @Id
    @Column("uid")
    val uid: Long? = null,

    @Column("auth_provider")
    val provider: AuthProvider,

    @Column("username")
    val username: String,

    @Column("nickname")
    val nickname: String,

    @Column("profile_image_url")
    val profileImageUrl: String? = null,

    @Column("role")
    val role: Role = Role.GUEST,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
)