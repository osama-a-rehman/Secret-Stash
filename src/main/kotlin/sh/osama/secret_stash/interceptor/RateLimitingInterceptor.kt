package sh.osama.secret_stash.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import sh.osama.secret_stash.exception.dto.ExceptionMessageDTO
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitingInterceptor (
    private val objectMapper: ObjectMapper,

    @Value("\${app.rate-limiter.capacity:}")
    private val capacity: Long,

    @Value("\${app.rate-limiter.replenish-rate:}")
    private val replenishRate: Long,

    @Value("\${app.rate-limiter.replenish-in-seconds:}")
    private val replenishTime: Long,
) : HandlerInterceptor {
    private val buckets: MutableMap<String, Bucket> = ConcurrentHashMap()

    private fun createNewBucket(): Bucket {
        val limit = Bandwidth.classic(capacity, Refill.greedy(replenishRate, Duration.ofSeconds(replenishTime)))
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val userKey = extractUserKey(request) ?: request.remoteAddr

        val bucket = buckets.computeIfAbsent(userKey) { createNewBucket() }

        return if (bucket.tryConsume(1)) { true } else {
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write(objectMapper.writer().writeValueAsString(ExceptionMessageDTO("Too many requests")))
            false
        }
    }

    private fun extractUserKey(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null

        if (!authHeader.startsWith("Bearer ")) return null

        val token = authHeader.substring(7)
        return token
    }
}