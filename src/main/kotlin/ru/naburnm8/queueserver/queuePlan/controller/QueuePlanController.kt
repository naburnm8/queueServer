package ru.naburnm8.queueserver.queuePlan.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import ru.naburnm8.queueserver.queuePlan.request.QueuePlanRequest
import ru.naburnm8.queueserver.queuePlan.response.QueuePlanResponse
import ru.naburnm8.queueserver.queuePlan.response.QueuePlanShortResponse
import ru.naburnm8.queueserver.queuePlan.service.QueuePlanService
import ru.naburnm8.queueserver.queuePlan.transporter.TransporterMapper
import ru.naburnm8.queueserver.security.JwtUtils
import java.time.Instant
import java.util.UUID


@RestController
@RequestMapping("/api")
class QueuePlanController (
    private val planService: QueuePlanService
) {


    @DeleteMapping("/disciplines/{disciplineId}/queuePlans/{queuePlanId}")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun deleteQueuePlan(@PathVariable disciplineId: UUID, @PathVariable queuePlanId: UUID) {
        val subject = JwtUtils.getSubject()
        planService.deletePlan(subject, queuePlanId, disciplineId)
    }


    @PostMapping("/disciplines/{disciplineId}/queuePlans")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun createQueuePlan(@PathVariable disciplineId: UUID, @RequestBody request: QueuePlanRequest): QueuePlanResponse {
        val subject = JwtUtils.getSubject()
        val created = planService.createPlan(TransporterMapper.toTransporter(request, subject, disciplineId))
        return TransporterMapper.map(created)
    }

    @GetMapping("/disciplines/{disciplineId}/queuePlans")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getQueuePlansByDiscipline(@PathVariable disciplineId: UUID): List<QueuePlanResponse> {
        val found = planService.getPlansByDiscipline(disciplineId)
        return found.map {transporter -> TransporterMapper.map(transporter)}
    }

    @PutMapping("/disciplines/{disciplineId}/queuePlans")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun updateQueuePlan(@PathVariable disciplineId: UUID, @RequestBody request: QueuePlanRequest): QueuePlanResponse {
        val subject = JwtUtils.getSubject()
        val updated = planService.updatePlan(TransporterMapper.toTransporter(request, subject, disciplineId))
        return TransporterMapper.map(updated)
    }

    @GetMapping("/queuePlans/my")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getMyQueuePlans(): List<QueuePlanResponse> {
        val subject = JwtUtils.getSubject()
        val found = planService.getPlansByTeacher(subject)
        return found.map {transporter -> TransporterMapper.map(transporter)}
    }


    @GetMapping("/queuePlans")
    fun getQueuePlans(): List<QueuePlanShortResponse> {
        val found = planService.getAllPlans()
        return found.map {transporter -> QueuePlanShortResponse(
            transporter.id,
            transporter.title,
            transporter.discipline,
            transporter.status,
            transporter.teacher,
            transporter.slotDurationMinutes,
            transporter.useTime,
            transporter.wTime,
            transporter.useDebts,
            transporter.wDebts,
            transporter.useAchievements,
            transporter.wAchievements
        ) }
    }

    @GetMapping("/queuePlans/short/{queuePlanId}")
    fun getShortQueuePlan(@PathVariable queuePlanId: UUID) : QueuePlanShortResponse {
        val found = planService.getShortPlanById(queuePlanId)
            return QueuePlanShortResponse(
                found.id,
                found.title,
                found.discipline,
                found.status,
                found.teacher,
                found.slotDurationMinutes,
                found.useTime,
                found.wTime,
                found.useDebts,
                found.wDebts,
                found.useAchievements,
                found.wAchievements
            )
    }

    @GetMapping("/queuePlans/{queuePlanId}")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getQueuePlanById(@PathVariable queuePlanId: UUID): QueuePlanResponse {
        return TransporterMapper.map(planService.findById(queuePlanId))
    }


    @PostMapping("/queuePlans/{queuePlanId}/activate")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun activate(@PathVariable queuePlanId: UUID) {
        val subject = JwtUtils.getSubject()
        planService.changeStatus(subject, queuePlanId, QueueStatus.ACTIVE)
    }

    @PostMapping("/queuePlans/{queuePlanId}/close")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun close(@PathVariable queuePlanId: UUID) {
        val subject = JwtUtils.getSubject()
        planService.changeStatus(subject, queuePlanId, QueueStatus.CLOSED)
    }

}