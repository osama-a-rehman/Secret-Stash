package sh.osama.secret_stash.notes.dto

import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CreateNoteRequest (
    @field:NotBlank(message = "title cannot be blank")
    val title: String,

    @field:NotBlank(message = "content cannot be blank")
    val content: String,
    val expiry: Instant?,
)
