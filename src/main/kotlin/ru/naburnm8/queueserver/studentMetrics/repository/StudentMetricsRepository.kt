package ru.naburnm8.queueserver.studentMetrics.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.studentMetrics.entity.StudentMetrics
import java.util.UUID

interface StudentMetricsRepository: JpaRepository<StudentMetrics, UUID> {

    @Query("SELECT sm FROM StudentMetrics sm WHERE sm.discipline = :disciplineId AND sm.student = :studentId")
    fun findByStudentIdAndDisciplineId(studentId: UUID, disciplineId: String): List<StudentMetrics>

    fun findByDisciplineId(disciplineId: UUID): List<StudentMetrics>

}