package sh.osama.secret_stash.rate_limiter

import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.proxy.ProxyManager
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class RedisRateLimitingService (
    private val proxyManager: ProxyManager<ByteArray>,
    private val bucketConfiguration: BucketConfiguration,
) : IRateLimitingService {
    override fun getBucket(fromRequest: HttpServletRequest): Bucket =
        proxyManager.builder().build(getUserKey(fromRequest).toByteArray(), bucketConfiguration)

    private fun getUserKey(request: HttpServletRequest): String {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication.name == "anonymousUser") return request.remoteAddr
        return authentication.name
    }
}