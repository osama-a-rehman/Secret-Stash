package sh.osama.secret_stash.notes.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sh.osama.secret_stash.notes.dto.CreateNoteRequest
import sh.osama.secret_stash.notes.dto.EditNoteRequest
import sh.osama.secret_stash.notes.dto.NoteDTO
import sh.osama.secret_stash.notes.service.NoteService

@RestController
@RequestMapping("/api/notes")
class NoteController (
    private val noteService: NoteService
) {
    @GetMapping("/latest-1000")
    fun getLatest1000Notes(): List<NoteDTO> = noteService.getLatest1000Notes()

    @PostMapping
    fun createNote(@Valid @RequestBody request: CreateNoteRequest): NoteDTO = noteService.createNote(request)

    @PatchMapping("/{id}")
    fun editNote(
        @PathVariable id: String,
        @Valid @RequestBody request: EditNoteRequest
    ): NoteDTO = noteService.editNote(id, request)

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: String) {
        noteService.deleteNote(id)
    }
}