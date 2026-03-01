package ru.naburnm8.queueserver.invitation.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.invitation.entity.Invitation
import ru.naburnm8.queueserver.invitation.repository.InvitationRepository
import ru.naburnm8.queueserver.invitation.transporter.InvitationTransporter
import ru.naburnm8.queueserver.invitation.transporter.TransporterMapper
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queuePlan.service.QueuePlanOwnershipService
import java.util.UUID

@Service
class InvitationService (
    private val invitationRepository: InvitationRepository,
    private val queuePlanRepository: QueuePlanRepository,
    private val queuePlanOwnershipService: QueuePlanOwnershipService,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository
) {
    private fun validate(code: String?, targetGroup: String?, targetStudentIds: List<UUID>?) {
        if (code == null && targetGroup == null && targetStudentIds == null) {
            throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
        }

    }


    @Transactional
    fun createInvitation(queuePlanId: UUID, requesterId: UUID, req: InvitationTransporter): InvitationTransporter {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        validate(req.code, req.targetGroup, req.targetStudentIds)

        if (req.id != null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val planRef = queuePlanRepository.getReferenceById(queuePlanId)
        val teacherRef = teacherRepository.getReferenceById(requesterId)

        val entity = Invitation(
            queuePlan = planRef,
            createdBy = teacherRef,
            enabled = req.enabled,
            code = req.code?.trim(),
            targetGroup = req.targetGroup?.trim()?.uppercase(),
            expiresAt = req.expiresAt,
            maxUses = req.maxUses,
            mode = req.mode
        )

        val ids = req.targetStudentIds.orEmpty()
        if (ids.isNotEmpty()) {
            val students = studentRepository.findAllById(ids)
            if (students.size != ids.distinct().size) throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")
            entity.targetStudents.addAll(students)

        }

        val saved = invitationRepository.save(entity)

        return TransporterMapper.toTransporter(saved)
    }

    @Transactional
    fun getAllInvitationsByQueuePlan(queuePlanId: UUID, requesterId: UUID): List<InvitationTransporter> {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)

        return invitationRepository.findAllByQueuePlanId(queuePlanId).map { TransporterMapper.toTransporter(it) }
    }

    @Transactional
    fun updateInvitation(queuePlanId: UUID, requesterId: UUID, req: InvitationTransporter): InvitationTransporter {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        validate(req.code, req.targetGroup, req.targetStudentIds)

        if (req.id == null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val entity = invitationRepository.findById(req.id).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_INVITATION}") }

        if (entity.queuePlan.id != queuePlanId) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        entity.enabled = req.enabled
        entity.code = req.code?.trim()
        entity.targetGroup = req.targetGroup?.trim()?.uppercase()
        entity.expiresAt = req.expiresAt
        entity.maxUses = req.maxUses
        entity.mode = req.mode

        val ids = req.targetStudentIds.orEmpty()
        if (ids.isNotEmpty()) {
            val students = studentRepository.findAllById(ids)
            if (students.size != ids.distinct().size) throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")
            entity.targetStudents.clear()
            entity.targetStudents.addAll(students)

        } else {
            entity.targetStudents.clear()
        }

        val saved = invitationRepository.save(entity)

        return TransporterMapper.toTransporter(saved)
    }

    @Transactional
    fun deleteInvitation(queuePlanId: UUID, requesterId: UUID, invitationId: UUID) {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)

        val entity = invitationRepository.findById(invitationId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_INVITATION}") }

        if (entity.queuePlan.id != queuePlanId) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        invitationRepository.delete(entity)
    }
}