package ru.naburnm8.queueserver.queueRule.request



import ru.naburnm8.queueserver.queueRule.entity.RuleType
import tools.jackson.databind.JsonNode

import java.util.UUID

data class QueueRuleRequest(
    val id: UUID? = null,
    val type: RuleType,
    val enabled: Boolean = true,
    val payload: JsonNode
)
