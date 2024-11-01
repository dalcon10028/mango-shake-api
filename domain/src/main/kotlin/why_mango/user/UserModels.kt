package why_mango.user

import why_mango.user.enums.*
import java.time.LocalDateTime

data class UserCreate(
    val provider: AuthProvider,
    val username: String,
    val nickname: String,
    val profileImageUrl: String?,
)

data class UserModel(
    val uid: Long,
    val provider: AuthProvider,
    val username: String,
    val nickname: String,
    val profileImageUrl: String?,
    val role : Role,
    val createdAt: LocalDateTime,
)