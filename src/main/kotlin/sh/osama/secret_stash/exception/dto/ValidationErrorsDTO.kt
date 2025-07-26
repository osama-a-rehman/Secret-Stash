package sh.osama.secret_stash.exception.dto

data class ValidationErrorsDTO (
    val message: String,
    val errors: List<ValidationErrorDTO>,
)

data class ValidationErrorDTO (
    val error: String,
)