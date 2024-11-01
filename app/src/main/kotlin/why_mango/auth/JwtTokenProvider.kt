package why_mango.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import why_mango.auth.model.AuthUser
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.user.UserModel
import why_mango.user.enums.Role
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) {
    private val logger = KotlinLogging.logger {}
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    suspend fun generateAccessToken(userInfo: UserModel): String {
        return generateToken(userInfo, jwtProperties.accessTokenExpiration)
    }

    suspend fun generateRefreshToken(userInfo: UserModel): String {
        return generateToken(userInfo, jwtProperties.refreshTokenExpiration)
    }

    fun validateToken(token: String): Boolean = try {
        Jwts.parser().verifyWith(secretKey).build()
        true
    } catch (e: MangoShakeException) {
        false
    }

    fun parseToken(token: String): AuthUser = try {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)

        @Suppress("UNCHECKED_CAST")
        AuthUser(
            uid = claims.payload["uid"] as Long,
            username = claims.payload["email"] as String,
            role = claims.payload["role"] as Role
        )
    } catch (e: Exception) {
        logger.error(e) { "Failed to parse token" }
        throw MangoShakeException(ErrorCode.AUTHENTICATION_FAILED, "Invalid token")
    }

    private suspend fun generateToken(userInfo: UserModel, validity: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + validity)

        return Jwts.builder()
            .subject(userInfo.username)
            .claims(
                mapOf(
                    "username" to userInfo.username,
                    "uid" to userInfo.uid,
                    "role" to userInfo.role
                )
            )
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }


    @ConfigurationProperties(prefix = "auth.jwt")
    data class JwtProperties(
        val secret: String,
        val accessTokenExpiration: Long,
        val refreshTokenExpiration: Long,
    )
}