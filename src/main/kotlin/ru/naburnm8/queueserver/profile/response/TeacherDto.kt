package ru.naburnm8.queueserver.profile.response

import java.util.UUID

data class TeacherDto(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val department: String,
    val telegram: String,
    val avatarUrl: String,
)
