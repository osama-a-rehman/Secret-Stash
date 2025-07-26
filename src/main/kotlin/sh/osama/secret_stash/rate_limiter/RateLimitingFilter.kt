package sh.osama.secret_stash.rate_limiter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import sh.osama.secret_stash.exception.dto.ExceptionMessageDTO

@Component
class RateLimitingFilter (
    private val objectMapper: ObjectMapper,
    private val rateLimitingService: RateLimitingService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val bucket = rateLimitingService.getBucket(request)

        val probe = bucket.tryConsumeAndReturnRemaining(1)

        if (probe.isConsumed) {
            response.setHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            filterChain.doFilter(request, response)
        } else {
            val waitForRefill = probe.nanosToWaitForRefill / 1_000_000_000
            response.setHeader("Retry-After", waitForRefill.toString())
            response.setHeader("X-Rate-Limit-Remaining", "0")
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write(objectMapper.writer().writeValueAsString(ExceptionMessageDTO("Too many requests")))
        }
    }
}