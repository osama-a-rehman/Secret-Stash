package sh.osama.secret_stash

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container

@ContextConfiguration(initializers = [TestContainersSetup.Initializer::class])
abstract class TestContainersSetup {
    companion object {
        @Container
        @JvmStatic
        internal val redisContainer = GenericContainer("redis:8.0.0-alpine")
            .withExposedPorts(6379)
            .withReuse(true)
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            redisContainer.start()

            TestPropertyValues.of(
                "spring.redis.host=${redisContainer.host}",
                "spring.redis.port=${redisContainer.getMappedPort(6379)}",
            ).applyTo(context.environment)
        }
    }
}