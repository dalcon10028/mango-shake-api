package why_mango

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ProjectConfig: AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)
}