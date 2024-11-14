package why_mango.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import why_mango.user.UserModel
import why_mango.user.enums.Privilege
import why_mango.user.enums.Role

class AuthUser(
    val uid : Long? = null,
    val role: Role? = null,
    val privileges: Set<Privilege>? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    private val username: String,
    private val attributes: MutableMap<String, Any> = mutableMapOf()
) : UserDetails, OAuth2User {
    override fun getName(): String = username

    override fun getAttributes(): MutableMap<String, Any> = attributes

    override fun getPassword(): String = ""

    override fun getUsername(): String = username

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf<GrantedAuthority>().apply {
        role?.privileges?.map {
            add(GrantedAuthority { "ROLE_${it.name}" })
        }
        role?.let { add(GrantedAuthority { "ROLE_${role.name}" }) }
    }

    companion object {
        fun from(user: UserModel): AuthUser {
            return AuthUser(
                uid = user.uid,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                username = user.username,
                role = user.role,
                privileges = user.role.privileges
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