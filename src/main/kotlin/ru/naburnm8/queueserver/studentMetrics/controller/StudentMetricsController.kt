package ru.naburnm8.queueserver.studentMetrics.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.JwtUtils
import ru.naburnm8.queueserver.studentMetrics.request.StudentMetricsRequest
import ru.naburnm8.queueserver.studentMetrics.response.StudentMetricsResponse
import ru.naburnm8.queueserver.studentMetrics.service.StudentMetricsService
import ru.naburnm8.queueserver.studentMetrics.transporter.StudentMetricsTransporterIn
import ru.naburnm8.queueserver.studentMetrics.transporter.TransporterMapper
import java.util.UUID

@RestController
@RequestMapping("/api/disciplines")
class StudentMetricsController (
    private val studentMetricsService: StudentMetricsService
) {
    private fun getSubject(): UUID {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        return subject
    }

    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    @GetMapping("/{disciplineId}/metrics")
    fun metricsByDiscipline(@PathVariable disciplineId: UUID): List<StudentMetricsResponse> {

        return studentMetricsService.metricsByDiscipline(disciplineId, getSubject()).map { transporter -> TransporterMapper.map(transporter)}
    }

    private fun upsertBody(disciplineId: UUID, studentId: UUID, req: StudentMetricsRequest): StudentMetricsResponse {
        val subject = getSubject()

        val inserted = studentMetricsService.upsert(
            StudentMetricsTransporterIn(
                id = null,
                disciplineId = disciplineId,
                studentId = studentId,
                teacherId = subject,
                debtsCount = req.debtsCount,
                personalAchievementsScore = req.personalAchievementsScore
            )
        )
        return TransporterMapper.map(inserted)
    }

    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    @PostMapping("/{disciplineId}/students/{studentId}/metrics")
    fun createStudentMetrics(@PathVariable disciplineId: UUID, @PathVariable studentId: UUID, @RequestBody req: StudentMetricsRequest): StudentMetricsResponse {
        return upsertBody(disciplineId, studentId, req)
    }

    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    @PutMapping("/{disciplineId}/student/{studentId}/metrics")
    fun updateStudentMetrics(@PathVariable disciplineId: UUID, @PathVariable studentId: UUID, @RequestBody req: StudentMetricsRequest): StudentMetricsResponse {
        return upsertBody(disciplineId, studentId, req)
    }

    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    @GetMapping("/{disciplineId}/student/{studentId}/metrics")
    fun getStudentMetrics(@PathVariable disciplineId: UUID, @PathVariable studentId: UUID): StudentMetricsResponse {
        val subject = getSubject()
        val found = studentMetricsService.findAnyMetricsByDisciplineAndStudent(studentId, disciplineId, subject)
        return TransporterMapper.map(found)
    }

    @GetMapping("/{disciplineId}/student/my")
    fun myMetricsByDiscipline(@PathVariable disciplineId: UUID): StudentMetricsResponse {
        val subject = getSubject()
        val foundList = studentMetricsService.myMetrics(subject, disciplineId)
        val found = foundList[0]
        return TransporterMapper.map(found)
    }

    @DeleteMapping("/student/metrics/{metricId}")
    fun deleteMetrics(@PathVariable metricId: UUID) {
        studentMetricsService.delete(metricId, getSubject())
    }

}