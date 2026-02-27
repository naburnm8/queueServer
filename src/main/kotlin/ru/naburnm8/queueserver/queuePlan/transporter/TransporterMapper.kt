package ru.naburnm8.queueserver.queuePlan.transporter

import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import ru.naburnm8.queueserver.queuePlan.request.QueuePlanRequest
import ru.naburnm8.queueserver.queuePlan.response.QueuePlanResponse
import java.time.Instant
import java.util.UUID

object TransporterMapper {
    fun toTransporter(request: QueuePlanRequest, requesterId: UUID, disciplineId: UUID): QueuePlanTransporter {
        return QueuePlanTransporter(
            request.id,
            disciplineId,
            requesterId,
            request.title,
            request.status,
            request.useDebts,
            request.wDebts,
            request.useTime,
            request.wTime,
            request.useAchievements,
            request.wAchievements,
        )
    }

    fun toTransporter(entity: QueuePlan): QueuePlanTransporter {
        return QueuePlanTransporter(
            entity.id,
            entity.discipline.id,
            entity.createdBy.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
            entity.title,
            entity.status,
            entity.useDebts,
            entity.wDebts,
            entity.useTime,
            entity.wTime,
            entity.useAchievements,
            entity.wAchievements,
        )
    }

    fun map(transporter: QueuePlanTransporter, createdAt: Instant): QueuePlanResponse {
        return QueuePlanResponse(
            id = transporter.id,
            transporter.disciplineId,
            transporter.createdByTeacherId,
            transporter.title,
            transporter.status,
            transporter.useDebts,
            wDebts = if(transporter.wDebts in 0.0..1.0) transporter.wDebts else 1.0,
            transporter.useTime,
            wTime = if(transporter.wTime in 0.0..1.0) transporter.wTime else 1.0,
            transporter.useAchievements,
            wAchievements = if(transporter.wAchievements in 0.0..1.0) transporter.wAchievements else 1.0,
            createdAt = createdAt,
        )
    }
}