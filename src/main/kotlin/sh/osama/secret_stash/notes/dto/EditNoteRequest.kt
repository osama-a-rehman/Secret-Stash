package sh.osama.secret_stash.notes.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank
import org.openapitools.jackson.nullable.JsonNullable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EditNoteRequest (
    @field:NotBlank(message = "cannot be blank")
    val title: JsonNullable<String> = JsonNullable.undefined(),

    @field:NotBlank(message = "cannot be blank")
    val content: JsonNullable<String> = JsonNullable.undefined(),

    val expiry: JsonNullable<Instant> = JsonNullable.undefined(),
)