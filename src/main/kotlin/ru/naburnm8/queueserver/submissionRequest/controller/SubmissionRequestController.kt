package ru.naburnm8.queueserver.submissionRequest.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.security.JwtUtils
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import ru.naburnm8.queueserver.submissionRequest.request.SubmissionRequestRequest
import ru.naburnm8.queueserver.submissionRequest.response.SubmissionRequestResponse
import ru.naburnm8.queueserver.submissionRequest.response.SubmissionRequestShortResponse
import ru.naburnm8.queueserver.submissionRequest.service.SubmissionRequestService
import ru.naburnm8.queueserver.submissionRequest.transporter.TransporterMapper
import java.util.UUID

@RestController
@RequestMapping("/api/queuePlans")
class SubmissionRequestController (
    private val submissionRequestService: SubmissionRequestService,
) {

    @GetMapping("/{queuePlanId}/requests/my")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun getMySubmissionRequest(@PathVariable queuePlanId: UUID): SubmissionRequestResponse {
        val subject = JwtUtils.getSubject()
        val found = submissionRequestService.getMyRequest(queuePlanId, subject)
        return TransporterMapper.map(found)
    }

    @GetMapping("/requests/my")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun getAllMySubmissionRequests() : List<SubmissionRequestResponse> {
        val subject = JwtUtils.getSubject()
        val found = submissionRequestService.getMyRequests(subject)
        return found.map { TransporterMapper.map(it) }
    }

    @PostMapping("/{queuePlanId}/requests/my")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun createSubmissionRequest(@PathVariable queuePlanId: UUID, @RequestBody req: SubmissionRequestRequest): SubmissionRequestResponse {
        val subject = JwtUtils.getSubject()
        val created = submissionRequestService.createForStudent(queuePlanId, subject, TransporterMapper.toTransporter(req))
        return TransporterMapper.map(created)
    }

    @PostMapping("/{queuePlanId}/requests/my/leave")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun leaveQueue(@PathVariable("queuePlanId") queuePlanId: UUID) {
        val subject = JwtUtils.getSubject()
        submissionRequestService.leaveQueue(queuePlanId, subject)

    }

    @PutMapping("/{queuePlanId}/requests/my")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun updateMySubmissionRequest(@PathVariable queuePlanId: UUID, @RequestBody req: SubmissionRequestRequest): SubmissionRequestResponse {
        val subject = JwtUtils.getSubject()
        val updated = submissionRequestService.updateForStudent(queuePlanId, subject, TransporterMapper.toTransporter(req))
        return TransporterMapper.map(updated)
    }

    @DeleteMapping("/{queuePlanId}/requests/my")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun deleteMySubmissionRequest(@PathVariable queuePlanId: UUID) {
        val subject = JwtUtils.getSubject()
        submissionRequestService.deleteForStudent(queuePlanId, subject)
    }

    @GetMapping("/{queuePlanId}/requests")
    @PreAuthorize("hasRole('ROLE_QOPERATOR')")
    fun getAllSubmissionRequests(@PathVariable queuePlanId: UUID, @RequestParam(required = false) status: SubmissionStatus? = null): List<SubmissionRequestResponse> {
        val subject = JwtUtils.getSubject()
        val found = submissionRequestService.getAllRequests(queuePlanId, subject, status)
        return found.map { transporter -> TransporterMapper.map(transporter) }
    }

    @GetMapping("/{queuePlanId}/requests/short")
    @PreAuthorize("hasRole('ROLE_QOPERATOR')")
    fun getAllSubmissionRequestsShort(@PathVariable queuePlanId: UUID): List<SubmissionRequestShortResponse> {
        val subject = JwtUtils.getSubject()
        val found = submissionRequestService.getAllRequestsShort(queuePlanId, subject)
        return found.map {
            SubmissionRequestShortResponse(
                id = it.first.id,
                studentId = it.first.studentId,
                studentName = "${it.second.lastName} ${it.second.firstName} ${it.second.patronymic}",
                avatarUrl = it.second.avatarUrl,
                status = it.first.status,
                totalMinutes = it.first.totalMinutes,
                queuePlanId = it.first.queuePlanId,
            )
        }
    }

    @PutMapping("/{queuePlanId}/requests/{requestId}/status")
    @PreAuthorize("hasRole('ROLE_QOPERATOR')")
    fun updateSubmissionRequestStatus(@PathVariable queuePlanId: UUID, @PathVariable requestId: UUID, @RequestParam status: SubmissionStatus) {
        val subject = JwtUtils.getSubject()
        submissionRequestService.changeStatus(queuePlanId, subject, requestId, status)
    }

}