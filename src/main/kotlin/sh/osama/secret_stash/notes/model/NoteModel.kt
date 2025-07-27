package sh.osama.secret_stash.notes.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import sh.osama.secret_stash.notes.dto.NoteDTO
import sh.osama.secret_stash.shared.model.BaseModel
import sh.osama.secret_stash.user.model.UserModel
import java.time.Instant

@Entity(name = "notes")
@Table(name = "notes")
class NoteModel (
    @Column(name = "title", nullable = false)
    var title: String,

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(name = "expiry")
    var expiry: Instant? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserModel,
) : BaseModel() {
    fun isExpired(): Boolean = expiry?.isBefore(Instant.now()) ?: false

    fun toDTO(): NoteDTO = NoteDTO(
        id = this.id,
        title = this.title,
        content = this.content,
        expiry = this.expiry,
        createdAt = this.createdAt,
        lastModifiedAt = this.lastModifiedAt,
    )
}