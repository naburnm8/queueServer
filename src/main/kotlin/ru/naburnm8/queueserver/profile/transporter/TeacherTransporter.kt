package ru.naburnm8.queueserver.profile.transporter

import java.util.UUID

data class TeacherTransporter(
    var id: UUID,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val department: String,
    val telegram: String?,
    val avatarUrl: String?
)
