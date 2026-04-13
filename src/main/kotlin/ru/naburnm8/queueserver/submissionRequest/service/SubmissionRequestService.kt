package ru.naburnm8.queueserver.submissionRequest.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.naburnm8.queueserver.discipline.repository.WorkTypeRepository
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.invitation.service.InvitationMatcherService
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.transporter.StudentTransporter
import ru.naburnm8.queueserver.queue.repository.QueueRuntimeStateRepository
import ru.naburnm8.queueserver.queue.service.QueueRuntimeService
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
    private val workTypeRepository: WorkTypeRepository,
    private val queueRuntimeService: QueueRuntimeService,
    private val queueRuntimeStateRepository: QueueRuntimeStateRepository
) {
    @Transactional
    fun leaveQueue(queuePlanId: UUID, requesterId: UUID) {
        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
            ?: throw RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        existing.status = SubmissionStatus.DEQUEUED

        queueRuntimeService.refresh(existing.queuePlan.id)
    }


    @Transactional
    fun getMyRequests(subject: UUID) : List<OutSubmissionRequestTransporter> {
        val existing = submissionRequestRepository.findAllByStudentUserId(subject)
        return existing.map { TransporterMapper.toTransporter(it) }
    }


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

        queueRuntimeService.refresh(saved.queuePlan.id)
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

        queueRuntimeService.refresh(saved.queuePlan.id)
        return TransporterMapper.toTransporter(saved)

    }

    @Transactional
    fun deleteForStudent(queuePlanId: UUID, requesterId: UUID) {
        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
            ?: throw RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        val runtime = queueRuntimeStateRepository.findById(existing.queuePlan.id).orElseThrow()
        if (runtime.currentRequest?.id == existing.id) {
            runtime.currentRequest = null
            runtime.takenAt = null
            queueRuntimeStateRepository.save(runtime)
        }

        submissionRequestRepository.delete(existing)

        queueRuntimeService.refresh(existing.queuePlan.id)
    }

    @Transactional
    fun getMyRequest(queuePlanId: UUID, requesterId: UUID): OutSubmissionRequestTransporter {
        val existing = submissionRequestRepository.findByQueuePlanIdAndStudentUserId(queuePlanId, requesterId)
            ?: throw RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        return TransporterMapper.toTransporter(existing)
    }


    // Teacher actions

    @Transactional
    fun changeStatus(queuePlanId: UUID, teacherId: UUID, submissionRequestId: UUID, newStatus: SubmissionStatus) {
        queuePlanOwnershipService.checkOwnership(queuePlanId, teacherId)
        val existing = submissionRequestRepository.findById(submissionRequestId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}") }

        if (newStatus == existing.status) {
            return
        }

        existing.status = newStatus
        submissionRequestRepository.save(existing)
        queueRuntimeService.refresh(existing.queuePlan.id)
    }

    @Transactional
    fun getAllRequests(queuePlanId: UUID, requesterId: UUID, status: SubmissionStatus?): List<OutSubmissionRequestTransporter> {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)

        return if (status == null ) submissionRequestRepository.findAllByQueuePlanId(queuePlanId).map { TransporterMapper.toTransporter(it) }
        else submissionRequestRepository.findAllByQueuePlanIdAndStatus(queuePlanId, status).map { TransporterMapper.toTransporter(it) }
    }

    @Transactional
    fun getAllRequestsShort(queuePlanId: UUID, requesterId: UUID) : List<Pair<OutSubmissionRequestTransporter, StudentTransporter>> {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)

        val all = submissionRequestRepository.findAllWithItems(queuePlanId)
        val students = all.map { StudentTransporter(
            id = it.student.userId ?: UUID(0,0),
            firstName = it.student.firstName,
            academicGroup = it.student.academicGroup,
            lastName = it.student.lastName,
            patronymic = it.student.patronymic,
            avatarUrl = it.student.avatarUrl,
            telegram = it.student.telegram,
        ) }

        val allMapped = all.map { TransporterMapper.toTransporter(it) }

        return allMapped.zip(students)


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