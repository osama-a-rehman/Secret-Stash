package sh.osama.secret_stash

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.get
import sh.osama.secret_stash.notes.repository.NoteRepository

@TestPropertySource(properties = [
    "app.rate-limiter.capacity=5",
    "app.rate-limiter.replenish-rate=1",
    "app.rate-limiter.replenish-in-seconds=60"
])
class RateLimitingTest (
    @Autowired private val noteRepository: NoteRepository
) : IntegrationTestSetup() {
    @Test
    fun `should rate limit requests after 5 requests`() {
        val user = aUser()
        repeat(5) {
            mockMvc.get("/api/notes/latest-1000") {
                withBodyRequest(withAuthentication(user))
            }.andExpect {
                status { isOk() }
            }
        }

        mockMvc.get("/api/notes/latest-1000") {
            withBodyRequest(withAuthentication(user))
        }.andExpect {
            status { isTooManyRequests() }
        }
    }
}