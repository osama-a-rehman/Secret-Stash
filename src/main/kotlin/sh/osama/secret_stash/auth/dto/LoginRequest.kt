package sh.osama.secret_stash.auth.dto

data class LoginRequest (
    val username: String,
    val password: String,
)