package ru.naburnm8.queueserver.queue.data

import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import java.time.Instant
import java.util.UUID

data class QueueSnapshot(
    val queuePlanId: UUID,
    val version: Long,
    val generatedAt: Instant,
    val current: QueueEntryView? = null,
    val entries: List<QueueEntryView>,
) {
    companion object {
        val empty = QueueSnapshot(
            queuePlanId = UUID(0, 0),
            version = 0,
            generatedAt = Instant.EPOCH,
            entries = emptyList(),
        )
    }
}
