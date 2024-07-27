package ymango.me.sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class SampleRestConfig {
    @Bean
    fun sampleRestApi(): SampleRestApi = WebClient.builder()
        .baseUrl("https://api.sampleapis.com")
        .build()
        .let { WebClientAdapter.create(it) }
        .let { HttpServiceProxyFactory.builderFor(it).build() }
        .createClient(SampleRestApi::class.java)
}