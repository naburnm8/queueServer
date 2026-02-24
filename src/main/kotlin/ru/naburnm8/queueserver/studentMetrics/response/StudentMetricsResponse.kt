package ru.naburnm8.queueserver.studentMetrics.response

import ru.naburnm8.queueserver.discipline.response.DisciplineDto
import ru.naburnm8.queueserver.profile.response.StudentDto
import ru.naburnm8.queueserver.profile.response.TeacherDto
import java.util.UUID

data class StudentMetricsResponse(
    val id: UUID,
    val discipline: DisciplineDto,
    val student: StudentDto,
    val teacher: TeacherDto,
    val debtsCount: Int,
    val personalAchievementsScore: Int
)
