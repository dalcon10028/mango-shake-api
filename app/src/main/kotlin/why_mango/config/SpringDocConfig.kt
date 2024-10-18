package why_mango.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Mango Shake API",
        version = "v1",
        description = "Mango Shake API"
    )
)
@Configuration
class SpringDocConfig