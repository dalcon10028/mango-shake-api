package why_mango.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Mango Shake API",
        version = "v1",
        description = "Mango Shake API"
    ),
    servers = [
        Server(url = "/", description = "Default Server URL")
    ]
)
@Configuration
class SpringDocConfig