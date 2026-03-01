package ru.naburnm8.queueserver.invitation.request

import java.time.Instant
import java.util.UUID


data class InvitationRequest(
    val id: UUID? = null,
    val code: String? = null,
    val targetGroup: String? = null,
    val targetStudentIds: List<UUID>? = null,
    val expiresAt: Instant = Instant.MAX,
    val enabled: Boolean = true,
    val maxUses: Int = Int.MAX_VALUE,
)
