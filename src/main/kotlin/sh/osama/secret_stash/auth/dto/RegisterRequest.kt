package sh.osama.secret_stash.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import sh.osama.secret_stash.validator.ValidPassword

data class RegisterRequest (
    @field:NotBlank(message = "cannot be blank")
    @field:Email(message = "not a valid email")
    val username: String?,

    @field:ValidPassword(message = "doesn't meet constraints")
    val password: String?,

    @field:NotBlank(message = "cannot be blank")
    @field:Size(min = 2, message = "length should be 2 or more characters")
    val name: String?,
)