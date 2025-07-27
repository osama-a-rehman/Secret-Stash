package sh.osama.secret_stash.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import sh.osama.secret_stash.exception.dto.ExceptionMessageDTO

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val objectMapper: ObjectMapper,
    private val userDetailsService: UserDetailsService,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/auth/**",
                    "/h2-console/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.oauth2ResourceServer {
                it.jwt {  }
                it.authenticationEntryPoint(authEntryPoint())
            }

        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authenticationManager = DaoAuthenticationProvider(userDetailsService)
        authenticationManager.setPasswordEncoder(passwordEncoder())

        return ProviderManager(authenticationManager)
    }

    @Bean
    fun jwtDecoder(@Value("\${app.jwt.secret}") secret: String): JwtDecoder {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(HS512).build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authEntryPoint(): AuthenticationEntryPoint = AuthenticationEntryPoint { request, response, authException ->
        response.status = HttpStatus.UNAUTHORIZED.value()

        if (authException is InvalidBearerTokenException && authException.message?.contains("expired") == true) {
            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            response.writer.write(objectMapper.writer().writeValueAsString(
                ExceptionMessageDTO(
                    message = "Invalid token: token expired"
                )
            ))
        }

    }
}