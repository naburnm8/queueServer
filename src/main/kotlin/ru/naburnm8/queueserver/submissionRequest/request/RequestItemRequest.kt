package ru.naburnm8.queueserver.submissionRequest.request

import java.util.UUID

data class RequestItemRequest(
    val workTypeId: UUID,
    val quantity: Int = 1,
    val minutesOverride: Int? = null,
)
