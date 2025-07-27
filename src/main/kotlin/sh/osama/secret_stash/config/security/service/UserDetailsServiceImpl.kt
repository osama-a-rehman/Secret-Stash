package sh.osama.secret_stash.config.security.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import sh.osama.secret_stash.user.repository.UserRepository

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username)?.let {
            User(
                it.username,
                it.password,
                emptyList(),
            )
        } ?: throw UsernameNotFoundException("User not found - $username")
    }
}