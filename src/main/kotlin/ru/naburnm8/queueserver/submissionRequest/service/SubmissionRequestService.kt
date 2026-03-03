package ru.naburnm8.queueserver.submissionRequest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.naburnm8.queueserver.discipline.repository.WorkTypeRepository
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.invitation.service.InvitationMatcherService
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queuePlan.service.QueuePlanOwnershipService
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequestItem
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import ru.naburnm8.queueserver.submissionRequest.repository.SubmissionRequestRepository
import ru.naburnm8.queueserver.submissionRequest.transporter.InSubmissionRequestTransporter
import ru.naburnm8.queueserver.submissionRequest.transporter.OutSubmissionRequestTransporter
import ru.naburnm8.queueserver.submissionRequest.transporter.RequestItemTransporter
import ru.naburnm8.queueserver.submissionRequest.transporter.TransporterMapper
import java.util.UUID


@Service
class SubmissionRequestService(
    private val submissionRequestRepository: SubmissionRequestRepository,
    private val queuePlanRepository: QueuePlanRepository,
    private val invitationMatcherService: InvitationMatcherService,
    private val queuePlanOwnershipService: QueuePlanOwnershipService,
    private val studentRepository: StudentRepository,
    private val workTypeRepository: WorkTypeRepository
) {

    @Transactional
    fun createForStudent(queuePlanId: UUID, requesterId: UUID, req: InSubmissionRequestTransporter): OutSubmissionRequestTransporter {
        if (req.id != null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        if (req.items.isEmpty()) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val plan = queuePlanRepository.findById(queuePlanId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_QUEUE_PLAN}") }

        if (plan.status == QueueStatus.CLOSED) throw RuntimeException("${InnerExceptionCode.QUEUE_CLOSED}")

        val student = studentRepository.findById(requesterId).orElseThrow { RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}") }


        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
        if (existing != null) throw RuntimeException("${InnerExceptionCode.SUBMISSION_ALREADY_EXISTS}")

        val invite = invitationMatcherService.matches(queuePlanId, studentId = requesterId, code = req.inviteCode, targetGroup = student.academicGroup)

        val newEntity = SubmissionRequest(
            queuePlan = plan,
            student = student,
            status = if (invite != null) SubmissionStatus.ENQUEUED else SubmissionStatus.PENDING,
        )

        addAllItems(newEntity, req.items)

        val saved = submissionRequestRepository.save(newEntity)

        if (invite != null) {
            invitationMatcherService.consume(invite)
        }

        return TransporterMapper.toTransporter(saved)

    }

    @Transactional
    fun updateForStudent(queuePlanId: UUID, requesterId: UUID, req: InSubmissionRequestTransporter): OutSubmissionRequestTransporter {
        if (req.id == null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        if (req.items.isEmpty()) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val existing = submissionRequestRepository.findById(req.id).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}") }

        if (existing.queuePlan.id != queuePlanId) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        if (existing.student.userId != requesterId) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        existing.clearItems()

        addAllItems(existing, req.items)

        val saved = submissionRequestRepository.save(existing)

        return TransporterMapper.toTransporter(saved)

    }

    @Transactional
    fun deleteForStudent(queuePlanId: UUID, requesterId: UUID) {
        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
            ?: throw RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        submissionRequestRepository.delete(existing)
    }

    @Transactional
    fun getMyRequest(queuePlanId: UUID, requesterId: UUID): OutSubmissionRequestTransporter {
        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
            ?: throw RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        return TransporterMapper.toTransporter(existing)
    }


    // Teacher actions

    @Transactional
    fun changeStatus(queuePlanId: UUID, teacherId: UUID, submissionStatusId: UUID, newStatus: SubmissionStatus) {
        queuePlanOwnershipService.checkOwnership(queuePlanId, teacherId)
        val existing = submissionRequestRepository.findById(submissionStatusId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}") }
        if (newStatus == SubmissionStatus.ENQUEUED || newStatus == SubmissionStatus.REJECTED) existing.status = newStatus
        else throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
        submissionRequestRepository.save(existing)
    }

    @Transactional
    fun getAllRequests(queuePlanId: UUID, requesterId: UUID, status: SubmissionStatus?): List<OutSubmissionRequestTransporter> {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)

        return if (status == null ) submissionRequestRepository.findAllByQueuePlanId(queuePlanId).map { TransporterMapper.toTransporter(it) }
        else submissionRequestRepository.findAllByQueuePlanIdAndStatus(queuePlanId, status).map { TransporterMapper.toTransporter(it) }
    }

    private fun addAllItems(entity: SubmissionRequest, items: List<RequestItemTransporter>) {
        for (item in items) {
            val workTypeExists = workTypeRepository.existsById(item.workTypeId)
            if (!workTypeExists) throw RuntimeException("${InnerExceptionCode.NO_SUCH_WORK_TYPE}")

            val workTypeRef = workTypeRepository.getReferenceById(item.workTypeId)

            val newItem = SubmissionRequestItem(
                request = entity,
                workType = workTypeRef,
                quantity = item.quantity,
                minutesOverride = item.minutesOverride,
            )

            entity.addItem(newItem)
        }
    }

}