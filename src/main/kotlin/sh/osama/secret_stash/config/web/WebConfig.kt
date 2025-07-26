package sh.osama.secret_stash.config.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import sh.osama.secret_stash.interceptor.RateLimitingInterceptor

@Configuration
class WebConfig (
    private val rateLimitingInterceptor: RateLimitingInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitingInterceptor).addPathPatterns("/api/**")
    }
}