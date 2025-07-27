package sh.osama.secret_stash.rate_limiter

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.Refill
import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import java.time.Duration

@Configuration
class RateLimitingConfig (
    @Value("\${app.rate-limiter.capacity:}")
    private val capacity: Long,

    @Value("\${app.rate-limiter.replenish-rate:}")
    private val replenishRate: Long,

    @Value("\${app.rate-limiter.replenish-in-seconds:}")
    private val replenishTime: Long,
) {
    @Bean
    fun bucket4jProxyManager(jedisPool: JedisPool): ProxyManager<ByteArray> {
        return JedisBasedProxyManager
            .builderFor(jedisPool)
            .withExpirationStrategy { key, _ -> Duration.ofMinutes(100).toMillis() }
            .build()
    }

    @Bean
    fun bucketConfiguration(): BucketConfiguration =
        BucketConfiguration.builder()
            .addLimit(Bandwidth.classic(capacity, Refill.greedy(replenishRate, Duration.ofSeconds(replenishTime))))
            .build()
}