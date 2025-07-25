package sh.osama.secret_stash.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sh.osama.secret_stash.exception.dto.ExceptionMessageDTO

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(EntryNotFoundException::class)
    fun handleEntryNotFoundException(ex: EntryNotFoundException): ResponseEntity<*> =
        ResponseEntity(
            ExceptionMessageDTO(
                ex.message
            ),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(EntryAlreadyExistsException::class)
    fun handleEntryAlreadyExistsException(ex: EntryAlreadyExistsException): ResponseEntity<*> =
        ResponseEntity(
            ExceptionMessageDTO(
                ex.message
            ),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(value = [BadCredentialsException::class])
    fun handleBadCredentialsException(ex: AuthenticationException): ResponseEntity<*> =
        ResponseEntity(
            ExceptionMessageDTO("Invalid Credentials"),
            HttpStatus.UNAUTHORIZED
        )
}