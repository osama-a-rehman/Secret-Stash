package sh.osama.secret_stash

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class IntegrationTestSetup {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    val jsonMapper = jacksonObjectMapper()

    protected fun MockHttpServletRequestDsl.withBodyRequest(bodyRequest: Any) {
        contentType = (MediaType.APPLICATION_JSON)
        content = jsonMapper.writeValueAsString(bodyRequest).replace("\r\n", "\n")
    }
}