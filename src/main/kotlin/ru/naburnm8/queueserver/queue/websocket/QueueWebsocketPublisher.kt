package ru.naburnm8.queueserver.queue.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class QueueWebsocketPublisher (
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun publishChanged(queuePlanId: UUID, version: Long) {
        val topic = "/topic/queue/$queuePlanId"
        messagingTemplate.convertAndSend(topic, version)
    }
}