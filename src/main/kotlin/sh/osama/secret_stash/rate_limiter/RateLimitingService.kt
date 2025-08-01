package sh.osama.secret_stash.rate_limiter

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

//@Service
class RateLimitingService (
    @Value("\${app.rate-limiter.capacity:}")
    private val capacity: Long,

    @Value("\${app.rate-limiter.replenish-rate:}")
    private val replenishRate: Long,

    @Value("\${app.rate-limiter.replenish-in-seconds:}")
    private val replenishTime: Long,
): IRateLimitingService {
    private val buckets: MutableMap<String, Bucket> = ConcurrentHashMap()

    override fun getBucket(fromRequest: HttpServletRequest): Bucket =
        buckets.computeIfAbsent(getUserKey(fromRequest)) { createBucket() }

    private fun createBucket(): Bucket {
        val limit = Bandwidth.classic(capacity, Refill.greedy(replenishRate, Duration.ofSeconds(replenishTime)))
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    private fun getUserKey(request: HttpServletRequest): String {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication.name == "anonymousUser") return request.remoteAddr
        return authentication.name
    }
}