package sh.osama.secret_stash.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest (
    @field:NotBlank(message = "cannot be blank")
    @field:Email(message = "not a valid email")
    val username: String?,

    @field:NotBlank(message = "cannot be blank")
    val password: String?,
)