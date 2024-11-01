package why_mango.user.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.user.entity.User

interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun findByUsername(username: String): User?
}