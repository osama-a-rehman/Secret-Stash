package sh.osama.secret_stash

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SecretStashApplication

fun main(args: Array<String>) {
	runApplication<SecretStashApplication>(*args)
}
