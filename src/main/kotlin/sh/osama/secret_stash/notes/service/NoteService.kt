package sh.osama.secret_stash.notes.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import sh.osama.secret_stash.exception.EntryNotFoundException
import sh.osama.secret_stash.notes.dto.CreateNoteRequest
import sh.osama.secret_stash.notes.dto.EditNoteRequest
import sh.osama.secret_stash.notes.dto.NoteDTO
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.notes.repository.NoteRepository
import sh.osama.secret_stash.user.service.UserService

@Service
class NoteService (
    private val userService: UserService,
    private val noteRepository: NoteRepository,
) {
    fun getLatest1000Notes(): List<NoteDTO> {
        val page = noteRepository.findLatestNotes(
            userService.getCurrentUser(),
            PageRequest.of(0, 1000)
        )

        return page.content.map { it.toDTO() }
    }

    fun createNote(request: CreateNoteRequest): NoteDTO = request.let {
        val title = it.title!!; val content = it.content!!

        noteRepository.save(NoteModel(
            title = title,
            content = content,
            expiry = it.expiry,
            user = userService.getCurrentUser()
        )).toDTO()
    }

    fun editNote(id: String, request: EditNoteRequest): NoteDTO {
        findNoteById(id).apply {
            if (request.title.isPresent)
                this.title = request.title.get()

            if (request.content.isPresent)
                this.content = request.content.get()

            if (request.expiry.isPresent)
                this.expiry = request.expiry.get()

            return noteRepository.save(this).toDTO()
        }
    }

    fun deleteNote(id: String) {
        findNoteById(id).let {
            noteRepository.delete(it)
        }
    }

    private fun findNoteById(id: String): NoteModel =
        noteRepository.findByIdOrNull(id) ?: throw EntryNotFoundException("Note not found with id: $id")
}