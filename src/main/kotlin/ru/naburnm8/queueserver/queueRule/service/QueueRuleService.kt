package ru.naburnm8.queueserver.queueRule.service


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queuePlan.service.QueuePlanOwnershipService
import ru.naburnm8.queueserver.queueRule.body.GroupBonusRuleBody
import ru.naburnm8.queueserver.queueRule.body.IdentifierBonusRuleBody
import ru.naburnm8.queueserver.queueRule.body.TimestampBonusRuleBody
import ru.naburnm8.queueserver.queueRule.entity.QueueRule
import ru.naburnm8.queueserver.queueRule.entity.RuleType
import ru.naburnm8.queueserver.queueRule.repository.QueueRuleRepository
import ru.naburnm8.queueserver.queueRule.transporter.RuleTransporter
import ru.naburnm8.queueserver.queueRule.transporter.TransporterMapper
import java.util.UUID

@Service
class QueueRuleService (
    private val objectMapper: ObjectMapper,
    private val ruleRepository: QueueRuleRepository,
    private val queuePlanRepository: QueuePlanRepository,
    private val queuePlanOwnershipService: QueuePlanOwnershipService
    ) {

    @Transactional
    fun addRule(queuePlanId: UUID, requesterId: UUID, request: RuleTransporter): RuleTransporter {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        validatePayload(request.type, request.payload)

        if (request.id != null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val queuePlan = queuePlanRepository.getReferenceById(queuePlanId)
        val entity = QueueRule(
            queuePlan = queuePlan,
            type = request.type,
            enabled = request.enabled,
            payload = objectMapper.writeValueAsString(request.payload)
        )
        ruleRepository.save(entity)

        return TransporterMapper.toTransporter(entity, objectMapper)
    }

    @Transactional
    fun updateRule(queuePlanId: UUID, requesterId: UUID, request: RuleTransporter): RuleTransporter {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        validatePayload(request.type, request.payload)

        if (request.id == null) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val found = ruleRepository.findById(request.id)

        if (found.isEmpty) throw RuntimeException("${InnerExceptionCode.NO_SUCH_RULE}")

        val entity = found.get()

        entity.payload = objectMapper.writeValueAsString(request.payload)
        entity.type = request.type
        entity.enabled = request.enabled

        ruleRepository.save(entity)

        return TransporterMapper.toTransporter(entity, objectMapper)
    }

    @Transactional
    fun deleteRule(queuePlanId: UUID, requesterId: UUID, ruleId: UUID) {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        ruleRepository.deleteById(ruleId)
    }

    @Transactional
    fun getRules(queuePlanId: UUID): List<RuleTransporter> {
        val found = ruleRepository.findAllByQueuePlanId(queuePlanId)
        return found.map {entity -> TransporterMapper.toTransporter(entity, objectMapper) }
    }

    @Transactional
    fun changeEnabled(queuePlanId: UUID, requesterId: UUID, ruleId: UUID, enabled: Boolean) {
        queuePlanOwnershipService.checkOwnership(queuePlanId, requesterId)
        val found = ruleRepository.findById(ruleId)
        if (found.isEmpty) throw RuntimeException("${InnerExceptionCode.NO_SUCH_RULE}")
        val entity = found.get()
        entity.enabled = enabled
        ruleRepository.save(entity)
    }




    private fun validatePayload(type: RuleType, payload: JsonNode) {
        try {
            when (type) {
                RuleType.GROUP_BONUS -> {
                    val p = objectMapper.convertValue(payload, GroupBonusRuleBody::class.java)
                    require(p.groups.isNotEmpty())
                    require(p.groups.all { it.isNotBlank() })
                    require((p.bonus in 0.0..1.0) && (p.bonus.isFinite()))
                }

                RuleType.IDENTIFIER_BONUS -> {
                    val p = objectMapper.convertValue(payload, IdentifierBonusRuleBody::class.java)
                    require(p.values.isNotEmpty())
                    require(p.values.all { it.isNotBlank() })
                    require((p.bonus in 0.0..1.0) && (p.bonus.isFinite()))
                }

                RuleType.TIMESTAMP_BONUS -> {
                    val p = objectMapper.convertValue(payload, TimestampBonusRuleBody::class.java)
                    require((p.bonus in 0.0..1.0) && (p.bonus.isFinite()))
                }

                RuleType.CUSTOM -> {
                    throw NotImplementedError("Custom rules are not implemented.")
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
        } catch (e: NotImplementedError) {
            throw e
        }
    }
}