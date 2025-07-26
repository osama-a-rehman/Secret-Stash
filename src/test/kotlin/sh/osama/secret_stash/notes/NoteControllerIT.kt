package sh.osama.secret_stash.notes

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.IntegrationTestSetup
import sh.osama.secret_stash.notes.dto.CreateNoteRequest
import sh.osama.secret_stash.notes.dto.EditNoteRequest
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.notes.repository.NoteRepository
import java.time.Instant

@Transactional
class NoteControllerIT (
    @Autowired private val noteRepository: NoteRepository,
) : IntegrationTestSetup() {
    @Test
    fun `should return latest 1000 notes`() {
        val user = aUser()
        val savedNote = noteRepository.save(
            NoteModel(
                title = "Test title",
                content = "Test content",
                expiry = Instant.now().plusSeconds(600),
                user = user,
            )
        )

        mockMvc.get("/api/notes/latest-1000") {
            withAuthentication(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("Test title") }
            jsonPath("$[0].content") { value("Test content") }
            jsonPath("$[0].expiry") { value(savedNote.expiry.toString()) }
        }
    }

    @Test
    fun `should not return expired notes`() {
        val user = aUser()
        val expiredNote = noteRepository.save(
            NoteModel(
                title = "Note 1",
                content = "Note 1 content",
                expiry = Instant.now().minusSeconds(600),
                user = user,
            )
        )
        val nonExpiredNote = noteRepository.save(
            NoteModel(
                title = "Note 2",
                content = "Note 2 content",
                expiry = Instant.now().plusSeconds(600),
                user = user,
            )
        )
        val noExpiryNote = noteRepository.save(
            NoteModel(
                title = "Note 3",
                content = "Note 3 content",
                user = user,
            )
        )

        mockMvc.get("/api/notes/latest-1000") {
            withAuthentication(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(2) }

            jsonPath("$[0].title") { value("Note 3") }
            jsonPath("$[0].content") { value("Note 3 content") }

            jsonPath("$[1].title") { value("Note 2") }
            jsonPath("$[1].content") { value("Note 2 content") }
        }
    }

    @Test
    fun `should create a note`() {
        val request = CreateNoteRequest(
            title = "Test title",
            content = "Test content",
            expiry = Instant.now().plusSeconds(600)
        )

        mockMvc.post("/api/notes") {
            withAuthentication()
            withBodyRequest(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("Test title") }
            jsonPath("$.content") { value("Test content") }
            jsonPath("$.expiry") { value(request.expiry.toString()) }
        }
    }

    @Test
    fun `should edit an existing note`() {
        val user = aUser()
        val savedNote = noteRepository.save(
            NoteModel(
                title = "Test title",
                content = "Test content",
                expiry = Instant.now().plusSeconds(600),
                user = user,
            )
        )

        mockMvc.patch("/api/notes/${savedNote.id}") {
            withAuthentication(user)
            withBodyRequest(EditNoteRequest(
                title = JsonNullable.of("New title"),
                content = JsonNullable.of("New content"),
            ))
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("New title")}
            jsonPath("$.content") { value("New content") }
        }
    }

    @Test
    fun `should delete an existing note`() {
        val user = aUser()
        val savedNote = noteRepository.save(
            NoteModel(
                title = "Test title",
                content = "Test content",
                expiry = Instant.now().plusSeconds(600),
                user = user,
            )
        )

        mockMvc.delete("/api/notes/${savedNote.id}") {
            withAuthentication()
        }.andExpect {
            status { isOk() }
        }

        assertNull(noteRepository.findByIdOrNull(savedNote.id), "Note was not deleted")
    }
}