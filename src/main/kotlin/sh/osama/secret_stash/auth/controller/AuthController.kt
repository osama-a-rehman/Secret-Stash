package sh.osama.secret_stash.auth.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sh.osama.secret_stash.auth.dto.LoginRequest
import sh.osama.secret_stash.auth.dto.LoginResponse
import sh.osama.secret_stash.auth.dto.RegisterRequest
import sh.osama.secret_stash.auth.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse = authService.login(request)

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): LoginResponse = authService.register(request)
}