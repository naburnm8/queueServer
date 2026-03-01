package ru.naburnm8.queueserver.queueRule.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.queueRule.entity.QueueRule
import java.util.UUID

interface QueueRuleRepository: JpaRepository<QueueRule, UUID> {
    fun findAllByQueuePlanId(planId: UUID): List<QueueRule>
    fun deleteAllByQueuePlanId(id: UUID)
}