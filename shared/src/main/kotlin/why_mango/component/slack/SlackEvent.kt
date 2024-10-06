package why_mango.component.slack

import com.slack.api.model.Field
import org.springframework.context.ApplicationEvent

data class SlackEvent (
    val topic: Topic,
    val title: String,
    val message: String,
    val color: Color = Color.GOOD,
    val fields: List<Field> = emptyList(),
) : ApplicationEvent(message)