package ru.naburnm8.queueserver.queue.data

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import java.util.UUID

data class QueueEntryView(
    val place: Int,
    val requestId: UUID,
    val studentId: UUID,
    val studentName: String,
    val studentAvatarUrl: String? = null,
    val totalMinutes: Int,
    val priority: Double,
    val status: SubmissionStatus,
)
