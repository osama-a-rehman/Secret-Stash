package sh.osama.secret_stash

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.extension.ExtendWith
import org.openapitools.jackson.nullable.JsonNullableModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import sh.osama.secret_stash.config.security.service.JwtTokenService
import sh.osama.secret_stash.helpers.anUniqueString
import sh.osama.secret_stash.notes.model.NoteModel
import sh.osama.secret_stash.notes.repository.NoteRepository
import sh.osama.secret_stash.user.model.UserModel
import sh.osama.secret_stash.user.repository.UserRepository
import java.time.Instant

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
abstract class IntegrationTestSetup : TestContainersSetup() {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jwtTokenService: JwtTokenService

    @Autowired
    protected lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var noteRepository: NoteRepository

    val jsonMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModules(JsonNullableModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)



    protected fun MockHttpServletRequestDsl.withBodyRequest(bodyRequest: Any) {
        contentType = (MediaType.APPLICATION_JSON)
        content = jsonMapper.writeValueAsString(bodyRequest).replace("\r\n", "\n")
    }

    protected fun MockHttpServletRequestDsl.withAuthentication(user: UserModel = createUser()) {
        val accessToken = jwtTokenService.generateToken(username = user.username)
        header("Authorization", "Bearer $accessToken")
    }

    protected fun MockHttpServletRequestDsl.withAuthentication(accessToken: String) {
        header("Authorization", "Bearer $accessToken")
    }

    protected fun createUser(
        withUsername: String = anUniqueString("username"),
        withPassword: String = anUniqueString("password"),
        withName: String = anUniqueString("name"),
    ): UserModel = userRepository.save(
        sh.osama.secret_stash.helpers.aUser(
            withUsername = withUsername,
            withPassword = passwordEncoder.encode(withPassword),
            withName = withName,
        )
    )

    protected fun createNote(
        withTitle: String = anUniqueString("title"),
        withContent: String = anUniqueString("content"),
        withExpiry: Instant? = null,
        withUser: UserModel,
    ): NoteModel = noteRepository.save(
        sh.osama.secret_stash.helpers.aNote(
            withTitle = withTitle,
            withContent = withContent,
            withExpiry = withExpiry,
            withUser = withUser,
        )
    ).also {
        noteRepository.flush()
    }
}