package why_mango.user

import why_mango.user.entity.*

fun UserCreate.toEntity(): User {
    return User(
        provider = provider,
        username = username,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
    )
}

fun User.toModel(): UserModel {
    assert(uid != null)
    assert(createdAt != null)
    return UserModel(
        uid = uid!!,
        provider = provider,
        username = username,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        role = role,
        createdAt = createdAt!!,
    )
}