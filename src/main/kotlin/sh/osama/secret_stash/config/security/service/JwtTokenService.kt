package sh.osama.secret_stash.config.security.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenService (
    @Value("\${app.jwt.secret:}")
    private val jwtSecret: String,

    @Value("\${app.jwt.expiration-in-ms:}")
    private val jwtExpirationMs: Long,
) {
    fun generateToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(Date().time + jwtExpirationMs))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .compact()
    }
}