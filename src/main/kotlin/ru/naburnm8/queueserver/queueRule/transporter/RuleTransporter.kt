package ru.naburnm8.queueserver.queueRule.transporter


import ru.naburnm8.queueserver.queueRule.entity.RuleType
import tools.jackson.databind.JsonNode
import java.util.UUID


data class RuleTransporter(
    val id: UUID? = null,
    val type: RuleType,
    val enabled: Boolean = true,
    val payload: JsonNode,
    val queuePlanId: UUID,
)
