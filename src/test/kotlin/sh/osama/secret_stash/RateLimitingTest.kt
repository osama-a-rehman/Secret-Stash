package sh.osama.secret_stash

import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.get

@TestPropertySource(properties = [
    "app.rate-limiter.capacity=5",
    "app.rate-limiter.replenish-rate=1",
    "app.rate-limiter.replenish-in-seconds=60"
])
class RateLimitingTest : IntegrationTestSetup() {
    @Test
    fun `should rate limit requests after 5 requests`() {
        val user = createUser()

        repeat(5) {
            val remaining = 5 - (it+1)
            mockMvc.get("/api/notes/latest-1000") {
                withAuthentication(user)
            }.andExpect {
                status { isOk() }
                header { string("X-Rate-Limit-Remaining", remaining.toString()) }
            }
        }

        mockMvc.get("/api/notes/latest-1000") {
            withBodyRequest(withAuthentication(user))
        }.andExpect {
            status { isTooManyRequests() }
        }
    }
}