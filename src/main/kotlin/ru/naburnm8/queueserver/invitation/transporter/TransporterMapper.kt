package ru.naburnm8.queueserver.invitation.transporter

import ru.naburnm8.queueserver.invitation.entity.Invitation
import ru.naburnm8.queueserver.invitation.request.InvitationRequest
import ru.naburnm8.queueserver.invitation.response.InvitationResponse
import ru.naburnm8.queueserver.profile.response.StudentDto
import java.time.Instant
import java.util.UUID

object TransporterMapper {

    fun toTransporter(entity: Invitation): InvitationTransporter {
        return InvitationTransporter(
            id = entity.id,
            code = entity.code,
            targetGroup = entity.targetGroup,
            targetStudentIds = entity.targetStudents.map {it.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000")},
            expiresAt = entity.expiresAt,
            enabled = entity.enabled,
            maxUses = entity.maxUses,
            usedCount = entity.usedCount,
            createdAt = entity.createdAt,
        )
    }

    fun toTransporter(req: InvitationRequest): InvitationTransporter {
        return InvitationTransporter(
            id = req.id,
            code = req.code,
            targetGroup = req.targetGroup,
            targetStudentIds = req.targetStudentIds,
            expiresAt = req.expiresAt,
            enabled = req.enabled,
            maxUses = req.maxUses,
            createdAt = Instant.now(),
            )
    }

    fun map(transporter: InvitationTransporter, queuePlanId: UUID): InvitationResponse {
        return InvitationResponse(
            id = transporter.id ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
            queuePlanId = queuePlanId,
            code = transporter.code,
            targetGroup = transporter.targetGroup,
            targetStudents = transporter.targetStudents ?: listOf(),
            expiresAt = transporter.expiresAt,
            enabled = transporter.enabled,
            maxUses = transporter.maxUses,
            usedCount = transporter.usedCount ?: 0,
            createdAt = transporter.createdAt
        )
    }

}