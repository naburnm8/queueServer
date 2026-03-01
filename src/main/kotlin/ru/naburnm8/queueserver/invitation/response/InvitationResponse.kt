package ru.naburnm8.queueserver.invitation.response

import ru.naburnm8.queueserver.profile.response.StudentDto
import java.time.Instant
import java.util.UUID

data class InvitationResponse(
    val id: UUID,
    val queuePlanId: UUID,
    val enabled: Boolean,
    val code: String? = null,
    val targetGroup: String? = null,
    val targetStudents: List<StudentDto> = listOf(),
    val createdAt: Instant,
    val expiresAt: Instant,
    val maxUses: Int,
    val usedCount: Int,
)
