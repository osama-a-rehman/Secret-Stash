package sh.osama.secret_stash.user.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sh.osama.secret_stash.exception.EntryNotFoundException
import sh.osama.secret_stash.user.dto.UserDTO
import sh.osama.secret_stash.user.model.UserModel
import sh.osama.secret_stash.user.repository.UserRepository

@Service
@Transactional
class UserService (
    private val userRepository: UserRepository
) {
    fun addUser(user: UserModel): UserDTO =
        userRepository.save(user).toDTO()

    fun getProfile(): UserDTO = getCurrentUser().toDTO()

    fun getCurrentUser(): UserModel {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username) ?: throw EntryNotFoundException("User not found")
    }

    fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)
}