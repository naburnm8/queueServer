package ru.naburnm8.queueserver.submissionRequest.transporter

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import java.time.Instant
import java.util.UUID

data class OutSubmissionRequestTransporter(
    val id: UUID,
    val queuePlanId: UUID,
    val studentId: UUID,
    val status: SubmissionStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val totalMinutes: Int,
    val items: List<RequestItemTransporter>
)
