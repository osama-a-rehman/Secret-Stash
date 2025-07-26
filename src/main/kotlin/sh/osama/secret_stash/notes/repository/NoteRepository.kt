package sh.osama.secret_stash.notes.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.user.model.UserModel

@Repository
interface NoteRepository : JpaRepository<NoteModel, String>, PagingAndSortingRepository<NoteModel, String> {
    @Query("""
        FROM notes n 
        WHERE 
            n.user = :user AND 
            (n.expiry IS NULL OR n.expiry > CURRENT_TIMESTAMP)
        ORDER BY n.createdAt DESC
    """)
    fun findLatestNotes(user: UserModel, pageable: Pageable): Page<NoteModel>
}