package sh.osama.secret_stash.auth.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sh.osama.secret_stash.auth.dto.*
import sh.osama.secret_stash.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse = authService.login(request)

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): LoginResponse = authService.register(request)

    @PostMapping("/refresh-token")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): RefreshTokenResponse =
        authService.refreshToken(request)

    @PostMapping("/logout")
    fun logout(@Valid @RequestBody request: RefreshTokenRequest) {
        authService.logout(request)
    }
}