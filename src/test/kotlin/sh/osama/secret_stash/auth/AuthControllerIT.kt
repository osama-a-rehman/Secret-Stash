package sh.osama.secret_stash.auth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.IntegrationTestSetup
import sh.osama.secret_stash.auth.dto.LoginRequest
import sh.osama.secret_stash.auth.dto.RegisterRequest
import sh.osama.secret_stash.auth.service.AuthService

@Transactional
class AuthControllerIT (
    @Autowired private val authService: AuthService,
) : IntegrationTestSetup() {
    @Test
    fun `should register a new user`() {
        mockMvc.post("/api/auth/register") {
            withBodyRequest(
                RegisterRequest(
                    username = "testuser@example.com",
                    password = "Password123!",
                    name = "Test User"
                )
            )
        }.andExpect {
            status { isOk() }
            jsonPath("$.user.username") { value("testuser@example.com") }
        }
    }

    @Test
    fun `should login registered user and return JWT token`() {
        authService.register(RegisterRequest(
            username = "testuser@example.com",
            password = "Password123!",
            name = "Test User"
        ))

        mockMvc.post("/api/auth/login") {
            withBodyRequest(LoginRequest(
                username = "testuser@example.com",
                password = "Password123!",
            ))
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
            jsonPath("$.token.accessToken") { exists() }
        }
    }

    @Test
    fun `should fail login with invalid credentials`() {
        mockMvc.post("/api/auth/login") {
            withBodyRequest(LoginRequest(
                username = "non-existing-user@test.com",
                password = "invalid-password"
            ))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should fail registration when user with email already exists`() {
        authService.register(RegisterRequest(
            username = "testuser@example.com",
            password = "Password123!",
            name = "Test User"
        ))

        mockMvc.post("/api/auth/register") {
            withBodyRequest(
                RegisterRequest(
                    username = "testuser@example.com",
                    password = "Password123!",
                    name = "New Name"
                )
            )
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("User already exist with username") }
        }
    }

    @Test
    fun `should fail access to protected end-point when JWT is expired`() {
        val expiredToken = """
            eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MkBnbWFpbC5jb20iLCJpYXQiOjE3NTM0NTUxNDksImV4cCI6MTc1MzQ1NTMyOX0.u2Sx-6DSsu3MO90TYvT0TyJkQkmhxDCHeUWBSVk2-jPW1-gPH34QcE4GyRAYTzriksOlgX-Hu3_UpuSHdPaFCg
        """.trimIndent()

        mockMvc.get("/api/notes/latest-1000") {
            withAuthentication(expiredToken)
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Invalid token: token expired") }
        }
    }
}