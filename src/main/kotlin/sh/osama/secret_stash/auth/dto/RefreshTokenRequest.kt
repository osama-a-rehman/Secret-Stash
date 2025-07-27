package sh.osama.secret_stash.auth.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest (
    @field:NotBlank(message = "cannot be blank")
    val refreshToken: String?,
)