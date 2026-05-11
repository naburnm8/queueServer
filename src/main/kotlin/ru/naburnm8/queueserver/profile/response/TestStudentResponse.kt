package ru.naburnm8.queueserver.profile.response

import java.util.UUID

data class TestStudentResponse(
    val email: String,
    val password: String,
    val studentId: UUID
)