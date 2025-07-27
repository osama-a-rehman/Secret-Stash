package sh.osama.secret_stash.config.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import redis.clients.jedis.JedisPool

@Configuration
class JedisConfig (
    @Value("\${spring.redis.host:}")
    private val redisHost: String,

    @Value("\${spring.redis.port:}")
    private val redisPort: Int,
) {
    @Bean
    fun jedisPool(): JedisPool {
        return JedisPool(redisHost, redisPort)
    }

    @Bean
    fun jedisConnectionFactory(): RedisConnectionFactory {
        val jedisClientConfig = JedisClientConfiguration.builder().usePooling().build()
        val standaloneConfig = RedisStandaloneConfiguration(redisHost, redisPort)
        return JedisConnectionFactory(standaloneConfig, jedisClientConfig)
    }

    @Bean
    fun redisTemplate(): StringRedisTemplate {
        return StringRedisTemplate(jedisConnectionFactory())
    }
}