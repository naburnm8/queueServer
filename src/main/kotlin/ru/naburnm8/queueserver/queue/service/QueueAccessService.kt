package ru.naburnm8.queueserver.queue.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.service.DisciplineOwnershipService
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.security.RoleName

import ru.naburnm8.queueserver.submissionRequest.repository.SubmissionRequestRepository
import java.util.UUID

@Service
class QueueAccessService (
    private val queuePlanRepository: QueuePlanRepository,
    private val submissionRequestRepository: SubmissionRequestRepository,
    private val disciplineOwnershipService: DisciplineOwnershipService
) {

    enum class TypeOfGrantedAuthority {
        QOPERATOR, QCONSUMER
    }

    fun check(queuePlanId: UUID, userId: UUID, authorities: Collection<GrantedAuthority>): TypeOfGrantedAuthority {
        val plan = queuePlanRepository.findById(queuePlanId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_QUEUE_PLAN}") }

        val isTeacher = authorities.any {it.authority == RoleName.ROLE_QOPERATOR.toString() }
        val isStudent = authorities.any {it.authority == RoleName.ROLE_QCONSUMER.toString() }

        if (isTeacher) {
            disciplineOwnershipService.checkOwnership(userId, plan.discipline.id)
            return TypeOfGrantedAuthority.QOPERATOR
        }

        if (isStudent) {
            val hasRequest = submissionRequestRepository.existsByQueuePlanIdAndStudentUserId(queuePlanId, userId)
            if (!hasRequest) throw RuntimeException("${InnerExceptionCode.ACCESS_DENIED}")
            return TypeOfGrantedAuthority.QCONSUMER
        }

        throw RuntimeException("${InnerExceptionCode.ACCESS_DENIED}")
    }

}