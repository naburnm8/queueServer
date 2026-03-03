package ru.naburnm8.queueserver.submissionRequest.response

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import java.time.Instant
import java.util.UUID

data class SubmissionRequestResponse(
    val id: UUID,
    val queuePlanId: UUID,
    val studentId: UUID,
    val status: SubmissionStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val totalMinutes: Int,
    val items: List<SubmissionRequestItemResponse>
)
