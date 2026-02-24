package ru.naburnm8.queueserver.studentMetrics.transporter

import ru.naburnm8.queueserver.discipline.response.DisciplineDto
import ru.naburnm8.queueserver.profile.response.StudentDto
import ru.naburnm8.queueserver.profile.response.TeacherDto
import ru.naburnm8.queueserver.studentMetrics.response.StudentMetricsResponse
import java.util.UUID

object TransporterMapper {
    fun map(transporter: StudentMetricsTransporterOut): StudentMetricsResponse {
        return StudentMetricsResponse(
            id = transporter.id,
            debtsCount = transporter.debtsCount,
            personalAchievementsScore = transporter.personalAchievementsScore,
            discipline = DisciplineDto(
                id = transporter.discipline.id,
                name = transporter.discipline.name,
            ),
            teacher = TeacherDto(
                id = transporter.teacher.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
                firstName = transporter.teacher.firstName,
                lastName = transporter.teacher.lastName,
                department = transporter.teacher.department,
                telegram = transporter.teacher.telegram ?: "",
                avatarUrl = transporter.teacher.avatarUrl ?: ""
            ),
            student = StudentDto(
                id = transporter.student.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
                firstName = transporter.student.firstName,
                lastName = transporter.student.lastName,
                academicGroup = transporter.student.academicGroup,
                telegram = transporter.student.telegram ?: "",
                avatarUrl = transporter.student.avatarUrl ?: ""
            ),
        )
    }
}