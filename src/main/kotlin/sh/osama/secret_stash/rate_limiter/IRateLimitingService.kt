package sh.osama.secret_stash.rate_limiter

import io.github.bucket4j.Bucket
import jakarta.servlet.http.HttpServletRequest

interface IRateLimitingService {
    fun getBucket(fromRequest: HttpServletRequest): Bucket
}