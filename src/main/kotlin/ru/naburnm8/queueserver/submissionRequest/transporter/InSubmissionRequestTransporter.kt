package ru.naburnm8.queueserver.submissionRequest.transporter

import java.util.UUID

data class InSubmissionRequestTransporter(
    val id: UUID? = null,
    val items: List<RequestItemTransporter>,
    val inviteCode: String? = null
)
