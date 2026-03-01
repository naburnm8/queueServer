package ru.naburnm8.queueserver.invitation.transporter

import ru.naburnm8.queueserver.invitation.entity.InvitationMode
import ru.naburnm8.queueserver.profile.response.StudentDto
import java.time.Instant
import java.util.UUID

data class InvitationTransporter(
    val id: UUID? = null,
    val code: String? = null,
    val targetGroup: String? = null,
    val targetStudentIds: List<UUID>? = null,
    val targetStudents: List<StudentDto>? = null,
    val createdAt: Instant,
    val expiresAt: Instant = Instant.MAX,
    val enabled: Boolean = true,
    val maxUses: Int = Int.MAX_VALUE,
    val usedCount: Int? = null,
    val mode: InvitationMode = InvitationMode.COMMON,
)
