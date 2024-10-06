package why_mango.component.slack

import com.ninjasquad.springmockk.MockkBean
import com.slack.api.Slack
import com.slack.api.webhook.Payload
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import why_mango.component.slack.SlackEventListener.SlackProperties.WebhookProperty

@SpringBootTest
class SlackEventListenerTest(
    private val applicationEventPublisher: ApplicationEventPublisher,
    @MockkBean private val slackProperties: SlackEventListener.SlackProperties
) : FunSpec({
    test("send slack message") {
        val slack = mockk<Slack>(relaxed = true)
        coEvery { slackProperties.webhook } returns mapOf(Topic.NOTIFICATION to WebhookProperty("url"))
        coEvery { slack.send("url", any<Payload>()) } returns mockk()

        applicationEventPublisher.publishEvent(SlackEvent(Topic.NOTIFICATION, "title", "message"))
    }
})