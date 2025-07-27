package sh.osama.secret_stash.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import sh.osama.secret_stash.shared.model.BaseModel
import sh.osama.secret_stash.user.dto.UserDTO

@Entity(name = "users")
@Table(name = "users")
class UserModel (
    @Column(name = "username", unique = true, nullable = false)
    var username: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "name", nullable = false)
    var name: String,
) : BaseModel() {
    fun toDTO(): UserDTO = UserDTO(
        id = this.id,
        username = this.username,
        name = this.name,
    )
}