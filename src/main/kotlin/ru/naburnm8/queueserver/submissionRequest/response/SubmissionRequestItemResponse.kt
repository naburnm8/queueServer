package ru.naburnm8.queueserver.submissionRequest.response

import java.util.UUID

data class SubmissionRequestItemResponse(
    val workTypeId: UUID,
    val workTypeName: String,
    val minutesPerOne: Int,
    val quantity: Int,
    val minutesOverride: Int?

)
