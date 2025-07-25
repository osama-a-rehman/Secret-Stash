package sh.osama.secret_stash.auth.dto

data class RegisterRequest (
    val username: String,
    val password: String,
    val name: String,
)