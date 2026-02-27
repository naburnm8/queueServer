package ru.naburnm8.queueserver.queuePlan.request

import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import java.time.Instant
import java.util.UUID

data class QueuePlanRequest (
    val id: UUID? = null,
    val title: String,
    val status: QueueStatus,
    val useDebts: Boolean,
    val wDebts: Double,
    val useTime: Boolean,
    val wTime: Double,
    val useAchievements: Boolean,
    val wAchievements: Double,
)