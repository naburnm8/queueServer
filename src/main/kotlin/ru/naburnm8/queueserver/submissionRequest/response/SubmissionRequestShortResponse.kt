package ru.naburnm8.queueserver.submissionRequest.response

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import java.util.UUID

data class SubmissionRequestShortResponse(
    val id: UUID,
    val queuePlanId: UUID,
    val studentId: UUID,
    val studentName: String,
    val avatarUrl: String? = null,
    val status: SubmissionStatus,
    val totalMinutes: Int,
)
