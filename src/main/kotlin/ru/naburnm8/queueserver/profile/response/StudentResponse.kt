package ru.naburnm8.queueserver.profile.response

import java.util.UUID

data class StudentResponse(
    var id: UUID,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val academicGroup: String,
    val telegram: String?,
    val avatarUrl: String?
)
