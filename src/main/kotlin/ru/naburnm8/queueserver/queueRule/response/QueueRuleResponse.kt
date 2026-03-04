package ru.naburnm8.queueserver.queueRule.response


import ru.naburnm8.queueserver.queueRule.entity.RuleType
import tools.jackson.databind.JsonNode
import java.util.UUID

data class QueueRuleResponse(
    val id: UUID,
    val type: RuleType,
    val enabled: Boolean,
    val queuePlanId: UUID,
    val payload: JsonNode
)
