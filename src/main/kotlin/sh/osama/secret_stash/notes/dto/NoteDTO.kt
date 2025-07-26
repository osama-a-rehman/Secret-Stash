package sh.osama.secret_stash.notes.dto

import java.time.Instant

data class NoteDTO (
    val id: String,
    val title: String,
    val content: String,
    val expiry: Instant?,
    val createdAt: Instant?,
    val lastModifiedAt: Instant?,
)
