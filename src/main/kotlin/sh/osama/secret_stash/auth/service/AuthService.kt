package sh.osama.secret_stash.auth.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.auth.dto.*
import sh.osama.secret_stash.config.security.service.JwtTokenService
import sh.osama.secret_stash.config.security.service.RefreshTokenService
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
    private val refreshTokenService: RefreshTokenService,
) {
    fun login(request: LoginRequest): LoginResponse {
        val username = request.username!!; val password = request.password!!

        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            username, password
        )

        val authentication = authenticationManager.authenticate(authenticationRequest)
        SecurityContextHolder.getContext().authentication = authentication

        val token = jwtService.generateToken(authentication.name)
        val refreshToken = refreshTokenService.createToken(authentication.name)

        return LoginResponse(
            accessToken = TokenDTO(token = token),
            refreshToken = TokenDTO(token = refreshToken),
            user = userService.getCurrentUser().toDTO(),
        )
    }

    fun register(request: RegisterRequest): LoginResponse {
        val username = request.username!!; val password = request.password!!; val name = request.name!!

        if (userService.existsByUsername(request.username))
            throw EntryAlreadyExistsException("User already exist with username")

        userService.addUser(UserModel(
            username = username,
            password = passwordEncoder.encode(password),
            name = name,
        ))

        return login(LoginRequest(username, password))
    }

    fun refreshToken(request: RefreshTokenRequest): RefreshTokenResponse {
        val token = request.refreshToken!!
        val username = refreshTokenService.verifyToken(token)

        return RefreshTokenResponse(
            accessToken = TokenDTO(token = jwtService.generateToken(username)),
        )
    }

    fun logout(request: RefreshTokenRequest) {
        val token = request.refreshToken!!
        refreshTokenService.revokeToken(token)
    }
}