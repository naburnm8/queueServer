package ru.naburnm8.queueserver.queueRule.transporter


import ru.naburnm8.queueserver.queueRule.entity.QueueRule
import ru.naburnm8.queueserver.queueRule.request.QueueRuleRequest
import ru.naburnm8.queueserver.queueRule.response.QueueRuleResponse
import tools.jackson.databind.ObjectMapper
import java.util.UUID

object TransporterMapper {

    fun toTransporter(entity: QueueRule, objectMapper: ObjectMapper): RuleTransporter {
        return RuleTransporter(
            id = entity.id,
            type = entity.type,
            enabled = entity.enabled,
            payload = objectMapper.readTree(entity.payload),
            queuePlanId = entity.queuePlan.id,
        )
    }

    fun toTransporter(req: QueueRuleRequest, queuePlanId: UUID): RuleTransporter {
        return RuleTransporter(
            id = req.id,
            type = req.type,
            enabled = req.enabled,
            payload = req.payload,
            queuePlanId = queuePlanId,
        )
    }

    fun map(transporter: RuleTransporter): QueueRuleResponse {
        return QueueRuleResponse(
            id = transporter.id ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
            type = transporter.type,
            enabled = transporter.enabled,
            payload = transporter.payload,
            queuePlanId = transporter.queuePlanId,
        )
    }

}