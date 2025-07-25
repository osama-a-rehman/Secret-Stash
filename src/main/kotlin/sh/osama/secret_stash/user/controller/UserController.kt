package sh.osama.secret_stash.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sh.osama.secret_stash.user.dto.UserDTO
import sh.osama.secret_stash.user.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController (
    private val userService: UserService,
) {
    @GetMapping("/profile")
    fun getProfile(): UserDTO = userService.getProfile()
}