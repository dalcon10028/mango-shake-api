package ymango.me

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MangoShakeApiApplication

fun main(args: Array<String>) {
    runApplication<MangoShakeApiApplication>(*args)
}
