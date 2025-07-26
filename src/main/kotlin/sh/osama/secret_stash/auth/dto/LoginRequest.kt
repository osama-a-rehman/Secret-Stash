package sh.osama.secret_stash.auth.dto

import jakarta.validation.constraints.Email

data class LoginRequest (
    @field:Email(message = "not a valid email")
    val username: String,

    val password: String,
)