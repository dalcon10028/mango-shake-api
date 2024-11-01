package why_mango.component.slack

import com.slack.api.Slack
import com.slack.api.model.Attachments.attachment
import com.slack.api.webhook.Payload
import com.slack.api.webhook.WebhookPayloads.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException

@Component
class SlackEventListener(
    private val applicationCoroutineScope: CoroutineScope,
    private val slackProperties: SlackProperties,
) {
    private val slack: Slack = Slack.getInstance()

    @EventListener(SlackEvent::class)
    fun slackEventListener(event: SlackEvent) = applicationCoroutineScope.launch {
        val url = slackProperties.webhook[event.topic]?.url ?: throw MangoShakeException(ErrorCode.ILLEGAL_STATE, "No webhook url for ${event.topic}")

        val payload: Payload = payload { p -> p
//            .blocks(
//                withBlocks {
//                    header {
//                        text("Mango Shake", emoji = true)
//                    }
//                    section {
//                        fields {
//                            event.fields.forEach { field ->
//                                markdownText("*${field.title}*: ${field.value}")
//                            }
//                        }
//                    }
//                    divider()
//                    context {
//                        elements {
//                            markdownText("Triggered by ${event.topic}")
//                        }
//                    }
//                }
//            )
            .attachments(
                listOf(attachment { a -> a
                    .pretext(event.title)
                    .title(event.title)
                    .color(event.color.value)
                    .fields(event.fields)
                })
            )
        }

        slack.send(url, payload)
    }

    @ConfigurationProperties(prefix = "slack")
    data class SlackProperties(
        val webhook: Map<Topic, WebhookProperty>,
    ) {
        data class WebhookProperty(
            val url: String,
        )
    }
}