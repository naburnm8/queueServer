package ru.naburnm8.queueserver.queue.service


import org.springframework.stereotype.Service
import ru.bmstu.naburnm8.adaptiveQueue.AdaptiveQueue
import ru.bmstu.naburnm8.adaptiveQueue.event.EventIn
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueParam
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.queue.data.QueueRequestModel
import ru.naburnm8.queueserver.queue.data.QueueSnapshot
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queueRule.body.GroupBonusRuleBody
import ru.naburnm8.queueserver.queueRule.body.IdentifierBonusRuleBody
import ru.naburnm8.queueserver.queueRule.body.IdentifierField
import ru.naburnm8.queueserver.queueRule.body.TimestampBonusRuleBody
import ru.naburnm8.queueserver.queueRule.entity.QueueRule
import ru.naburnm8.queueserver.queueRule.entity.RuleType
import ru.naburnm8.queueserver.queueRule.repository.QueueRuleRepository
import ru.naburnm8.queueserver.studentMetrics.repository.StudentMetricsRepository
import ru.naburnm8.queueserver.submissionRequest.repository.SubmissionRequestRepository
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.naburnm8.queueserver.queue.data.QueueEntryView
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.UUID
import kotlin.collections.emptyList

@Service
class QueueViewService (
    private val queuePlanRepository: QueuePlanRepository,
    private val submissionRequestRepository: SubmissionRequestRepository,
    private val studentMetricsRepository: StudentMetricsRepository,
    private val queueRuleRepository: QueueRuleRepository,
    private val objectMapper: ObjectMapper,
) {
    fun buildSnapshot(queuePlanId: UUID, version: Long): QueueSnapshot {
        val plan = queuePlanRepository.findById(queuePlanId).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_QUEUE_PLAN}") }

        val enqueuedRequests = submissionRequestRepository.findEnqueuedWithItems(queuePlanId)

        val studentIds = enqueuedRequests.map {it.student.userId!!}.distinct()

        val metrics = if (studentIds.isEmpty()) emptyList()
        else studentMetricsRepository.findBatch(plan.discipline.id, studentIds)

        val metricsByStudentId = metrics.associateBy { it.student.userId!! }

        val models = enqueuedRequests.map {req ->
            val total = req.totalMinutes()
            QueueRequestModel(
                request = req,
                student = req.student,
                metrics = metricsByStudentId[req.student.userId!!],
                totalMinutes = total
            )
        }

        val params = buildParamsFromPlan(plan)
        val runtimeRules = buildRuntimeRules(queueRuleRepository.findAllByQueuePlanId(plan.id))

        val entries = models.map {m -> QueueEntry(m, params) }

        val aq = AdaptiveQueue<QueueRequestModel> (
            entries = entries,
            rules = runtimeRules,
        )


        val peekAll = aq.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll

        val out = peekAll.entries.map { placed ->
            val model = placed.entry.entry.model
            QueueEntryView(
                place = placed.place,
                requestId = model.request.id,
                studentId = model.student.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
                studentName = "${model.student.lastName} ${model.student.firstName} ${model.student.patronymic}",
                studentAvatarUrl = model.student.avatarUrl,
                totalMinutes = model.totalMinutes,
                priority = placed.entry.priority,
                status = model.request.status,
            )
        }

        return QueueSnapshot(
            queuePlanId = plan.id,
            version = version,
            generatedAt = Instant.now(),
            entries = out
        )
    }

    private fun buildParamsFromPlan(plan: QueuePlan): List<QueueParam<QueueRequestModel>> {
        val params = mutableListOf<QueueParam<QueueRequestModel>>()

        if (plan.useDebts) {
            params += QueueParam(
                name = "debts",
                weight = plan.wDebts,
                compute = { m ->
                    val debts =  m.metrics?.debtsCount ?: (Int.MAX_VALUE shl 8)
                    1.0 / (debts.toDouble() + 1.0)
                }
            )
        }

        if (plan.useTime) {
            params += QueueParam(
                name = "time",
                weight = plan.wTime,
                compute = {m ->
                    plan.slotDurationMinutes / (m.totalMinutes.toDouble())
                }
            )
        }

        if (plan.useAchievements) {
            params += QueueParam(
                name = "achievements",
                weight = plan.wAchievements,
                compute = {m ->
                    val a = m.metrics?.personalAchievementsScore ?: 0
                    a.toDouble() / plan.discipline.personalAchievementsScoreLimit
                }
            )
        }

        return params
    }

    private fun buildRuntimeRules(rules: List<QueueRule>): List<PriorityRule<QueueRequestModel>> {
        val out = mutableListOf<PriorityRule<QueueRequestModel>>()

        for (rule in rules) {
            if (!rule.enabled) continue

            when (rule.type) {
                RuleType.CUSTOM -> {
                    throw NotImplementedError()
                }

                RuleType.TIMESTAMP_BONUS -> {
                    val payload = objectMapper.readValue(rule.payload, TimestampBonusRuleBody::class.java)
                    val begin = payload.begin.toInstant()
                    val end = payload.end.toInstant()
                    out += PriorityRule(
                        identifier = rule.id,
                        condition = {m -> m.request.createdAt in begin..end },
                        calculate = {_ -> payload.bonus }
                    )
                }

                RuleType.GROUP_BONUS -> {
                    val payload = objectMapper.readValue(rule.payload, GroupBonusRuleBody::class.java)
                    out += PriorityRule(
                        identifier = rule.id,
                        condition = {m -> m.student.academicGroup in payload.groups },
                        calculate = {_ -> payload.bonus }
                    )
                }

                RuleType.IDENTIFIER_BONUS -> {
                    val payload = objectMapper.readValue(rule.payload, IdentifierBonusRuleBody::class.java)
                    out += PriorityRule(
                        identifier = rule.id,
                        condition = {m ->
                            when (payload.field) {
                                IdentifierField.EMAIL -> {payload.values.contains(m.student.user.email)}
                                IdentifierField.TELEGRAM -> {payload.values.contains(m.student.telegram)}
                                IdentifierField.FULL_NAME -> {
                                    val fullName = "${m.student.lastName} ${m.student.firstName} ${m.student.patronymic}"
                                    payload.values.contains(fullName)
                                }
                            }
                        },
                        calculate = {_ -> payload.bonus }
                    )
                }
            }
        }

        return out
    }
}