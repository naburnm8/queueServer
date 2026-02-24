package ru.naburnm8.queueserver.studentMetrics.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.service.OwnershipService
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.studentMetrics.entity.StudentMetrics
import ru.naburnm8.queueserver.studentMetrics.repository.StudentMetricsRepository
import ru.naburnm8.queueserver.studentMetrics.transporter.StudentMetricsTransporterOut
import ru.naburnm8.queueserver.studentMetrics.transporter.StudentMetricsTransporterIn
import java.util.UUID

@Service
class StudentMetricsService (
    private val studentMetricsRepository: StudentMetricsRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val disciplineRepository: DisciplineRepository,
    private val ownershipService: OwnershipService
) {

    @Transactional
    fun upsert(data: StudentMetricsTransporterIn): StudentMetricsTransporterOut {
        ownershipService.checkOwnership(data.teacherId, data.disciplineId)
        if (data.id != null) {
            // update
            val inDb = studentMetricsRepository.findById(data.id)
            if (inDb.isEmpty) throw RuntimeException("${InnerExceptionCode.NO_SUCH_STUDENT_METRIC}")

            val inDbGet = inDb.get()
            inDbGet.debtsCount = data.debtsCount
            inDbGet.personalAchievementsScore = data.personalAchievementsScore
            val saved = studentMetricsRepository.save(inDbGet)
            return StudentMetricsTransporterOut(
                id = saved.id,
                discipline = saved.discipline,
                teacher = saved.teacher,
                student = saved.student,
                debtsCount = saved.debtsCount,
                personalAchievementsScore = saved.personalAchievementsScore,
            )
        } else {
            // insert
            val teacherRef = teacherRepository.getReferenceById(data.teacherId)
            val studentRef = studentRepository.getReferenceById(data.studentId)
            val disciplineRef = disciplineRepository.getReferenceById(data.disciplineId)

            val existing = studentMetricsRepository.findByStudentIdAndDisciplineId(data.studentId, data.disciplineId)
            if (existing == null) {
                val saved = studentMetricsRepository.save(
                    StudentMetrics(
                        teacher = teacherRef,
                        discipline = disciplineRef,
                        student = studentRef,
                        debtsCount = data.debtsCount,
                        personalAchievementsScore = data.personalAchievementsScore,
                    )
                )
                return StudentMetricsTransporterOut(
                    id = saved.id,
                    discipline = saved.discipline,
                    teacher = saved.teacher,
                    student = saved.student,
                    debtsCount = saved.debtsCount,
                    personalAchievementsScore = saved.personalAchievementsScore,
                )
            } else {
                throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
            }

        }
    }

    //TODO: Метрики по дисциплине (Преподаватель)
    //TODO: Мои метрики (Студент)

}