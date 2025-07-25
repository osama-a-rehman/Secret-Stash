package sh.osama.secret_stash.auth.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.auth.dto.LoginRequest
import sh.osama.secret_stash.auth.dto.LoginResponse
import sh.osama.secret_stash.auth.dto.RegisterRequest
import sh.osama.secret_stash.auth.dto.TokenDTO
import sh.osama.secret_stash.config.security.service.JwtTokenService
import sh.osama.secret_stash.exception.EntryAlreadyExistsException
import sh.osama.secret_stash.user.model.UserModel
import sh.osama.secret_stash.user.service.UserService

@Service
@Transactional
class AuthService (
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val jwtService: JwtTokenService,
) {
    fun login(request: LoginRequest): LoginResponse {
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            request.username, request.password
        )

        val authentication = authenticationManager.authenticate(authenticationRequest)
        SecurityContextHolder.getContext().authentication = authentication

        val token = jwtService.generateToken(authentication.name)

        return LoginResponse(
            token = TokenDTO(
                accessToken = token
            ),
            user = userService.getCurrentUser().toDTO(),
        )
    }

    fun register(request: RegisterRequest): LoginResponse {
        val (username, password, name) = request

        if (userService.existsByUsername(request.username))
            throw EntryAlreadyExistsException("User already exist with username")

        userService.addUser(UserModel(
            username = username,
            password = passwordEncoder.encode(password),
            name = name,
        ))

        return login(LoginRequest(username, password))
    }
}