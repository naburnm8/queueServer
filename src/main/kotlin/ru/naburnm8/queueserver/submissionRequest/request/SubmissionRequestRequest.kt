package ru.naburnm8.queueserver.submissionRequest.request

import java.util.UUID

data class SubmissionRequestRequest(
    val id: UUID? = null,
    val items: List<RequestItemRequest>,
    val inviteCode: String? = null
)
