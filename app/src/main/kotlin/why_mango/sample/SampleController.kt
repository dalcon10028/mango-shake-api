package why_mango.sample

import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic

@RestController
@RequestMapping("/sample")
class SampleController(
    private val publisher: ApplicationEventPublisher
) {

    @PostMapping("/slack")
    suspend fun slack() {
        publisher.publishEvent(SlackEvent(
            topic = Topic.NOTIFICATION,
            title = "title",
            message = "message"
        ))
    }
}