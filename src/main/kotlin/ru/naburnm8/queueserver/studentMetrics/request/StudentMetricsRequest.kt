package ru.naburnm8.queueserver.studentMetrics.request

import java.util.UUID

data class StudentMetricsRequest(
    val id: UUID? = null,
    val debtsCount: Int,
    val personalAchievementsScore: Int
)
