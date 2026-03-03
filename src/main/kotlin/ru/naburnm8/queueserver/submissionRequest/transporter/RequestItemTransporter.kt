package ru.naburnm8.queueserver.submissionRequest.transporter

import java.util.UUID

data class RequestItemTransporter(
    val workTypeId: UUID,
    val quantity: Int = 1,
    val minutesOverride: Int? = null
)
