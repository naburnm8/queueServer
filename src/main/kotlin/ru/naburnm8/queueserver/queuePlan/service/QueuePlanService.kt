package ru.naburnm8.queueserver.queuePlan.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.response.DisciplineDto
import ru.naburnm8.queueserver.discipline.service.OwnershipService
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.profile.response.TeacherDto
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queuePlan.transporter.QueuePlanShortTransporter
import ru.naburnm8.queueserver.queuePlan.transporter.QueuePlanTransporter
import ru.naburnm8.queueserver.queuePlan.transporter.TransporterMapper
import java.util.UUID

@Service
class QueuePlanService (
    private val queuePlanRepository: QueuePlanRepository,
    private val teacherRepository: TeacherRepository,
    private val disciplineRepository: DisciplineRepository,
    private val ownershipService: OwnershipService
) {

    @Transactional
    fun createPlan(plan: QueuePlanTransporter): QueuePlanTransporter {
        if (plan.id != null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
        ownershipService.checkOwnership(plan.createdByTeacherId, plan.disciplineId)
        val disciplineRef = disciplineRepository.getReferenceById(plan.disciplineId)
        val teacherRef = teacherRepository.getReferenceById(plan.createdByTeacherId)
        val planEntity = QueuePlan(
            discipline = disciplineRef,
            createdBy = teacherRef,
            title = plan.title,
            useTime = plan.useTime,
            wTime = plan.wTime,
            useDebts = plan.useDebts,
            wDebts = plan.wDebts,
            useAchievements = plan.useAchievements,
            wAchievements = plan.wAchievements,
            status = QueueStatus.DRAFT
        )

        queuePlanRepository.save(planEntity)

        return TransporterMapper.toTransporter(planEntity)
    }

    @Transactional
    fun updatePlan(plan: QueuePlanTransporter): QueuePlanTransporter {
        if (plan.id == null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
        ownershipService.checkOwnership(plan.createdByTeacherId, plan.disciplineId)
        val planEntity = queuePlanRepository.findById(plan.id).get()
        planEntity.title = plan.title
        planEntity.useTime = plan.useTime
        planEntity.useDebts = plan.useDebts
        planEntity.wTime = plan.wTime
        planEntity.useAchievements = plan.useAchievements
        planEntity.wDebts = plan.wDebts
        planEntity.wAchievements = plan.wAchievements

        queuePlanRepository.save(planEntity)

        return TransporterMapper.toTransporter(planEntity)
    }

    @Transactional
    fun changeStatus(requesterId: UUID, planId: UUID, newStatus: QueueStatus) {
        val planEntityOptional = queuePlanRepository.findById(planId)
        if (planEntityOptional.isEmpty) throw RuntimeException("${InnerExceptionCode.NO_SUCH_QUEUE_PLAN}")
        val planEntity = planEntityOptional.get()
        if (requesterId != planEntity.createdBy.userId) throw RuntimeException("${InnerExceptionCode.QUEUE_PLAN_NOT_OWNED}")
        planEntity.status = newStatus

        queuePlanRepository.save(planEntity)
    }

    @Transactional
    fun getAllPlans(): List<QueuePlanShortTransporter> {
        val inDb = queuePlanRepository.findAll()
        return inDb.map {entity -> QueuePlanShortTransporter(
            id = entity.id,
            title = entity.title,
            status = entity.status,
            discipline = DisciplineDto(
                id = entity.discipline.id,
                name = entity.discipline.name
            ),
            teacher = TeacherDto(
                id = entity.createdBy.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
                firstName = entity.createdBy.firstName,
                lastName = entity.createdBy.lastName,
                department = entity.createdBy.department,
                telegram = entity.createdBy.telegram ?: "",
                avatarUrl = entity.createdBy.avatarUrl ?: ""
            )
        ) }
    }

    @Transactional
    fun getPlansByTeacher(teacherId: UUID): List<QueuePlanTransporter> {
        val found = queuePlanRepository.findAllByCreatedByUserId(teacherId)
        return found.map { entity -> TransporterMapper.toTransporter(entity) }
    }

    @Transactional
    fun getPlansByDiscipline(disciplineId: UUID): List<QueuePlanTransporter> {
        val found = queuePlanRepository.findAllByDisciplineId(disciplineId)
        return found.map { entity -> TransporterMapper.toTransporter(entity) }
    }

}