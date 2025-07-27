package sh.osama.secret_stash.config.security.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import sh.osama.secret_stash.exception.InvalidRefreshTokenException
import java.time.Duration
import java.util.*

@Service
class RefreshTokenService (
    @Value("\${app.jwt.refresh-token.expiration-in-ms:}")
    private val refreshTokenExpirationMs: Long,
    private val redisTemplate: StringRedisTemplate,
) {
    fun createToken(username: String): String {
        val token = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().toByteArray())
        redisTemplate.opsForValue().set(token, username, Duration.ofMillis(refreshTokenExpirationMs))
        return token
    }

    fun verifyToken(token: String): String {
        val username = redisTemplate.opsForValue().get(token)
        return username ?: throw InvalidRefreshTokenException("Invalid Refresh token")
    }

    fun revokeToken(token: String) {
        redisTemplate.delete(token)
    }
}