package sh.osama.secret_stash.notes

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import sh.osama.secret_stash.helpers.aNote
import sh.osama.secret_stash.helpers.aUser
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.notes.repository.NoteRepository
import sh.osama.secret_stash.user.repository.UserRepository
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant

@DataJpaTest
class NoteRepositoryTest (
    @Autowired private val noteRepository: NoteRepository,
    @Autowired private val userRepository: UserRepository,
) {
    @Test
    fun `should return non-expired notes for the user ordered by createdAt desc`() {
        val user1 = userRepository.save(aUser())
        val user2 = userRepository.save(aUser())

        val expiredNote = createNote(
            aNote(
                withExpiry = Instant.now().minusSeconds(3600),
                withUser = user1,
            )
        )

        val futureNote = createNote(
            aNote(
                withExpiry = Instant.now().plusSeconds(3600),
                withUser = user1,
            )
        )

        val noExpiryNote = createNote(
            aNote(withUser = user1)
        )

        val otherUserNote = createNote(
            aNote(withUser = user2)
        )

        val page = noteRepository.findLatestNotes(user1, PageRequest.of(0, 10))
        val results = page.content

        expectThat(results)
            .hasSize(2)
            .map { it.id }
            .and {
                not().contains(otherUserNote.id) // not from other user
                not().contains(expiredNote.id) // not expired
            }
            .isEqualTo(listOf(noExpiryNote.id, futureNote.id)) // sorted descending by creation date
    }

    private fun createNote(note: NoteModel): NoteModel {
        val savedNote = noteRepository.save(note)
        noteRepository.flush()

        return savedNote
    }
}