package sh.osama.secret_stash.notes

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import sh.osama.secret_stash.helpers.aUser
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.notes.repository.NoteRepository
import sh.osama.secret_stash.notes.service.NoteService
import sh.osama.secret_stash.user.service.UserService
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.map

@ExtendWith(MockitoExtension::class)
class NoteServiceTest {
    private var userService: UserService = mock<UserService>()
    private var noteRepository: NoteRepository = mock<NoteRepository>()

    private lateinit var noteService: NoteService

    @BeforeEach
    fun initialize() {
        noteService = NoteService(userService, noteRepository)
    }

    @Test
    fun `should return notes of user`() {
        val user = aUser()
        val notes = listOf(
            NoteModel(title = "Note1", content = "Text", user = user),
            NoteModel(title = "Note2", content = "Text", user = user)
        )
        val page = PageImpl(notes)

        whenever(userService.getCurrentUser()).thenReturn(user)
        whenever(noteRepository.findLatestNotes(eq(user), any())).thenReturn(page)

        val result = noteService.getLatest1000Notes()

        expectThat(result)
            .hasSize(2)
            .map { it.title }.isEqualTo(listOf("Note1", "Note2"))
    }
}