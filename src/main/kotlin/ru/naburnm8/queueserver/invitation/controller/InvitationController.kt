package ru.naburnm8.queueserver.invitation.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.invitation.request.InvitationRequest
import ru.naburnm8.queueserver.invitation.response.InvitationResponse
import ru.naburnm8.queueserver.invitation.service.InvitationService
import ru.naburnm8.queueserver.invitation.transporter.TransporterMapper
import ru.naburnm8.queueserver.security.JwtUtils
import java.util.UUID

@RestController
@RequestMapping("/api/queuePlans")
class InvitationController (
    private val invitationService: InvitationService,
) {

    @PostMapping("/{queuePlanId}/invitations")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun createInvitation(@PathVariable queuePlanId: UUID, @RequestBody req: InvitationRequest): InvitationResponse {
        val subject = JwtUtils.getSubject()
        val created = invitationService.createInvitation(queuePlanId, subject, TransporterMapper.toTransporter(req))
        return TransporterMapper.map(created, queuePlanId)
    }

    @GetMapping("/{queuePlanId}/invitations")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getAllInvitationsByQueuePlan(@PathVariable queuePlanId: UUID): List<InvitationResponse> {
        val subject = JwtUtils.getSubject()
        val found = invitationService.getAllInvitationsByQueuePlan(queuePlanId, subject)
        return found.map { transporter -> TransporterMapper.map(transporter, queuePlanId) }
    }


    @PutMapping("/{queuePlanId}/invitations")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun updateInvitation(@PathVariable queuePlanId: UUID, @RequestBody req: InvitationRequest): InvitationResponse {
        val subject = JwtUtils.getSubject()
        val updated = invitationService.updateInvitation(queuePlanId, subject, TransporterMapper.toTransporter(req))
        return TransporterMapper.map(updated, queuePlanId)
    }

    @DeleteMapping("/{queuePlanId}/invitations/{invitationId}")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun deleteInvitation(@PathVariable queuePlanId: UUID, @PathVariable invitationId: UUID) {
        val subject = JwtUtils.getSubject()
        invitationService.deleteInvitation(queuePlanId, subject, invitationId)
    }

}