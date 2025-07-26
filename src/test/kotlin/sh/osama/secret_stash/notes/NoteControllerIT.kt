package sh.osama.secret_stash.notes

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.IntegrationTestSetup
import sh.osama.secret_stash.notes.dto.CreateNoteRequest
import sh.osama.secret_stash.notes.dto.EditNoteRequest
import java.time.Instant

@Transactional
class NoteControllerIT : IntegrationTestSetup() {
    @Test
    fun `should return latest 1000 notes`() {
        val user = createUser()
        val savedNote = createNote(
            withTitle = "Test title",
            withContent = "Test content",
            withExpiry = Instant.now().plusSeconds(600),
            withUser = user,
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
        val user = createUser()

        val expiredNote = createNote(
            withTitle = "Note 1",
            withContent = "Note 1 content",
            withExpiry = Instant.now().minusSeconds(600),
            withUser = user,
        )

        val nonExpiredNote = createNote(
            withTitle = "Note 2",
            withContent = "Note 2 content",
            withExpiry = Instant.now().plusSeconds(600),
            withUser = user,
        )
        val noExpiryNote = createNote(
            withTitle = "Note 3",
            withContent = "Note 3 content",
            withUser = user,
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
    fun `should not return other users' notes`() {
        val user1 = createUser()
        val user2 = createUser()

        val savedNote1 = createNote(
            withTitle = "User 1 note title",
            withContent = "User 1 note content",
            withExpiry = Instant.now().plusSeconds(1200),
            withUser = user1,
        )
        val savedNote2 = createNote(
            withTitle = "User 2 note title",
            withContent = "User 2 note content",
            withExpiry = Instant.now().plusSeconds(600),
            withUser = user2,
        )

        mockMvc.get("/api/notes/latest-1000") {
            withAuthentication(user1)
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("User 1 note title") }
            jsonPath("$[0].content") { value("User 1 note content") }
            jsonPath("$[0].expiry") { value(savedNote1.expiry.toString()) }
        }

        mockMvc.get("/api/notes/latest-1000") {
            withAuthentication(user2)
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("User 2 note title") }
            jsonPath("$[0].content") { value("User 2 note content") }
            jsonPath("$[0].expiry") { value(savedNote2.expiry.toString()) }
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
        val user = createUser()
        val savedNote = createNote(
            withTitle = "Test title",
            withContent = "Test content",
            withExpiry = Instant.now().plusSeconds(600),
            withUser = user,
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
        val user = createUser()
        val savedNote = createNote(
            withTitle = "Test title",
            withContent = "Test content",
            withExpiry = Instant.now().plusSeconds(600),
            withUser = user,
        )

        mockMvc.delete("/api/notes/${savedNote.id}") {
            withAuthentication()
        }.andExpect {
            status { isOk() }
        }

        assertNull(noteRepository.findByIdOrNull(savedNote.id), "Note was not deleted")
    }
}