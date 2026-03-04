package ru.naburnm8.queueserver.queueRule.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.queueRule.request.QueueRuleRequest
import ru.naburnm8.queueserver.queueRule.response.QueueRuleResponse
import ru.naburnm8.queueserver.queueRule.service.QueueRuleService
import ru.naburnm8.queueserver.queueRule.transporter.RuleTransporter
import ru.naburnm8.queueserver.queueRule.transporter.TransporterMapper
import ru.naburnm8.queueserver.security.JwtUtils
import java.util.UUID

@RestController
@RequestMapping("/api/queuePlans")
class QueueRuleController (
    private val ruleService: QueueRuleService
) {

    @PostMapping("/{queuePlanId}/rules")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun addRule(@PathVariable queuePlanId: UUID, @RequestBody req: QueueRuleRequest): QueueRuleResponse {
        val subject = JwtUtils.getSubject()

        val created = ruleService.addRule(queuePlanId, subject, TransporterMapper.toTransporter(req, queuePlanId))

        return TransporterMapper.map(created)
    }

    @GetMapping("/{queuePlanId}/rules")
    fun getRules(@PathVariable queuePlanId: UUID): List<QueueRuleResponse> {
        val found = ruleService.getRules(queuePlanId)
        return found.map { TransporterMapper.map(it) }
    }


    @PutMapping("/{queuePlanId}/rules")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun updateRule(@PathVariable queuePlanId: UUID, @RequestBody req: QueueRuleRequest): QueueRuleResponse {
        val subject = JwtUtils.getSubject()

        val updated = ruleService.updateRule(queuePlanId, subject, TransporterMapper.toTransporter(req, queuePlanId))

        return TransporterMapper.map(updated)
    }

    @DeleteMapping("/{queuePlanId}/rules/{ruleId}")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun removeRule(@PathVariable queuePlanId: UUID, @PathVariable ruleId: UUID) {
        val subject = JwtUtils.getSubject()
        ruleService.deleteRule(queuePlanId, subject, ruleId)
    }

    @PostMapping("/{queuePlanId}/rules/{ruleId}/enable")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun enableRule(@PathVariable queuePlanId: UUID, @PathVariable ruleId: UUID) {
        val subject = JwtUtils.getSubject()
        ruleService.changeEnabled(queuePlanId, subject, ruleId, true)
    }

    @PostMapping("/{queuePlanId}/rules/{ruleId}/disable")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun disableRule(@PathVariable queuePlanId: UUID, @PathVariable ruleId: UUID) {
        val subject = JwtUtils.getSubject()
        ruleService.changeEnabled(queuePlanId, subject, ruleId, false)
    }


}