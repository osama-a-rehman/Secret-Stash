package sh.osama.secret_stash.auth.dto

import sh.osama.secret_stash.user.dto.UserDTO

data class LoginResponse (
    val user: UserDTO,
    val accessToken: TokenDTO,
    val refreshToken: TokenDTO,
)