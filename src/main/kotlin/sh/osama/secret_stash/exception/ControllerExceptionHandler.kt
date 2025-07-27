package sh.osama.secret_stash.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import sh.osama.secret_stash.exception.dto.ExceptionMessageDTO
import sh.osama.secret_stash.exception.dto.ValidationErrorDTO
import sh.osama.secret_stash.exception.dto.ValidationErrorsDTO

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleEmptyBody(ex: HttpMessageNotReadableException): ResponseEntity<*> =
        ResponseEntity(
            ExceptionMessageDTO("Request body is missing or malformed: ${ex.message}"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<*> =
        ResponseEntity(ValidationErrorsDTO(
            message = "Validation errors have occurred",
            errors = ex.bindingResult.fieldErrors.map { ValidationErrorDTO("${it.field}: ${it.defaultMessage}") }
        ), HttpStatus.BAD_REQUEST)
}