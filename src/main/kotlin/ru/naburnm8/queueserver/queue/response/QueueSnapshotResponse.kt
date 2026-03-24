package ru.naburnm8.queueserver.queue.response

import ru.naburnm8.queueserver.queue.data.QueueEntryView
import java.time.Instant
import java.util.UUID

data class QueueSnapshotResponse(
    val queuePlanId: UUID,
    val version: Long,
    val generatedAt: Instant,
    val current: QueueEntryViewResponse?,
    val entries: List<QueueEntryViewResponse>
)
