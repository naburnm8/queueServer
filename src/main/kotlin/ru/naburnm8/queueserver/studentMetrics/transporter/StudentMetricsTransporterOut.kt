package ru.naburnm8.queueserver.studentMetrics.transporter

import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

data class StudentMetricsTransporterOut(
    val id : UUID,
    val discipline: Discipline,
    val teacher: Teacher,
    val student: Student,
    val debtsCount: Int,
    val personalAchievementsScore: Int
)
