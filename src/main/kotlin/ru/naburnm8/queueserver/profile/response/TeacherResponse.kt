package ru.naburnm8.queueserver.profile.response

import java.util.UUID

data class TeacherResponse (
    var id: UUID,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val department: String,
    val telegram: String?,
    val avatarUrl: String?
)