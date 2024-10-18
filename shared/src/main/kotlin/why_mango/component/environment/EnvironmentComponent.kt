package why_mango.component.environment

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnvironmentComponent(
    private val environment: Environment
) {
    enum class Profile { LOCAL }

    fun getActiveProfiles(): Array<String> {
        return environment.activeProfiles
    }

    fun isLocal(): Boolean = getActiveProfiles().map { it.uppercase() }.contains(Profile.LOCAL.name)
}