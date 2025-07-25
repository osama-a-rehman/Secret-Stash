package sh.osama.secret_stash.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sh.osama.secret_stash.user.model.UserModel

@Repository
interface UserRepository : JpaRepository<UserModel, String> {
    fun findByUsername(username: String): UserModel?
    fun existsByUsername(username: String): Boolean
}