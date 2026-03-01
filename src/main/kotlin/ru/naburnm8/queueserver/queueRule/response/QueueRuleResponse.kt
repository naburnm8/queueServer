package ru.naburnm8.queueserver.queueRule.response

import com.fasterxml.jackson.databind.JsonNode
import ru.naburnm8.queueserver.queueRule.entity.RuleType
import java.util.UUID

data class QueueRuleResponse(
    val id: UUID,
    val type: RuleType,
    val enabled: Boolean,
    val queuePlanId: UUID,
    val payload: JsonNode
)
