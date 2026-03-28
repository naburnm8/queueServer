package ru.naburnm8.queueserver.queuePlan.transporter

import ru.naburnm8.queueserver.discipline.response.DisciplineDto
import ru.naburnm8.queueserver.profile.response.TeacherDto
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import java.util.UUID

data class QueuePlanShortTransporter(
    val id: UUID,
    val title: String,
    val discipline: DisciplineDto,
    val status: QueueStatus,
    val teacher: TeacherDto,
    val slotDurationMinutes: Int,
    val useTime: Boolean,
    val wTime: Double,
    val useDebts: Boolean,
    val wDebts: Double,
    val useAchievements: Boolean,
    val wAchievements: Double
)
