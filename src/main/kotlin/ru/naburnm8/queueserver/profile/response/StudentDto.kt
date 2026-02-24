package ru.naburnm8.queueserver.profile.response

import java.util.UUID

data class StudentDto(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val academicGroup: String,
    val telegram: String,
    val avatarUrl: String,
)
