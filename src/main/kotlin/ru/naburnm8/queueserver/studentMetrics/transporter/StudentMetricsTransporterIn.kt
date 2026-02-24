package ru.naburnm8.queueserver.studentMetrics.transporter

import java.util.UUID

data class StudentMetricsTransporterIn(
    val id: UUID? = null,
    val disciplineId: UUID,
    val teacherId: UUID,
    val studentId: UUID,
    val debtsCount: Int,
    val personalAchievementsScore: Int
)
