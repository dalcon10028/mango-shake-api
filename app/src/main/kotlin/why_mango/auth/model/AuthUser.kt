package why_mango.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import why_mango.user.UserModel
import why_mango.user.enums.Role

class AuthUser(
    val uid : Long? = null,
    val role: Role? = null,
    private val username: String,
    private val attributes: MutableMap<String, Any> = mutableMapOf()
) : UserDetails, OAuth2User {
    override fun getName(): String = username

    override fun getAttributes(): MutableMap<String, Any> = attributes

    override fun getPassword(): String = ""

    override fun getUsername(): String = username

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = role?.privileges
        ?.map { privilege -> GrantedAuthority { privilege.name } }
        ?.toMutableList() ?: mutableListOf()

    companion object {
        fun from(user: UserModel): AuthUser {
            return AuthUser(
                uid = user.uid,
                username = user.username,
                role = user.role
            )
        }

        fun from(user: OAuth2User): AuthUser {
            return AuthUser(
                username = user.name,
                attributes = user.attributes
            )
        }
    }
}