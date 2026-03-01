package ru.naburnm8.queueserver.queuePlan.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queueRule.transporter.RuleTransporter
import java.util.UUID

@Service
class QueuePlanOwnershipService (
    private val queuePlanRepository: QueuePlanRepository,
) {
    @Transactional
    fun checkOwnership(queuePlanId: UUID, requesterId: UUID,) {
        val decision = queuePlanRepository.isCreator(queuePlanId, requesterId)
        if (!decision) throw RuntimeException("${InnerExceptionCode.QUEUE_PLAN_NOT_OWNED}")
    }
}