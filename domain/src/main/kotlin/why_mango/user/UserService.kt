package why_mango.user

import org.springframework.stereotype.Service
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    suspend fun create(user: UserCreate): UserModel = userRepository.save(user.toEntity()).toModel()

    suspend fun find(uid: Long): UserModel =
        userRepository.findById(uid)?.toModel() ?: throw MangoShakeException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with uid: $uid")

    suspend fun findByUsername(username: String): UserModel? = userRepository.findByUsername(username)?.toModel()
}