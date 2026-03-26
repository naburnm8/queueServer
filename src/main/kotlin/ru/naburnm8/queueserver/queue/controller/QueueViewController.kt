package ru.naburnm8.queueserver.queue.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.queue.data.DataMapper
import ru.naburnm8.queueserver.queue.response.QueueSnapshotResponse
import ru.naburnm8.queueserver.queue.service.QueueAccessService
import ru.naburnm8.queueserver.queue.service.QueueInteractionService
import ru.naburnm8.queueserver.queue.service.QueueRuntimeService
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import java.util.UUID

@RestController
@RequestMapping("/api/queuePlans")
class QueueViewController (
    private val queueRuntimeService: QueueRuntimeService,
    private val queueAccessService: QueueAccessService,
    private val queueInteractionService: QueueInteractionService
) {
    @GetMapping("/{queuePlanId}/view")
    fun view(@PathVariable queuePlanId: UUID, @RequestParam(required = false, defaultValue = "false") force: Boolean, authentication: Authentication): QueueSnapshotResponse {
        val userId = UUID.fromString((authentication.principal as Jwt).subject)

        val granted = queueAccessService.check(queuePlanId, userId, authentication.authorities)

        val toReturn = if (granted == QueueAccessService.TypeOfGrantedAuthority.QCONSUMER) DataMapper.map(queueRuntimeService.getOrBuild(queuePlanId))
        else {
            if (force) DataMapper.map(queueRuntimeService.refresh(queuePlanId))
            else DataMapper.map(queueRuntimeService.getOrBuild(queuePlanId))
        }

        val status = queueInteractionService.getStatus(queuePlanId)

        toReturn.status = status

        return toReturn
    }

    @PostMapping("/{queuePlanId}/takeNext")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun takeNext(@PathVariable queuePlanId: UUID, authentication: Authentication) {
        val userId = UUID.fromString((authentication.principal as Jwt).subject)

        queueAccessService.check(queuePlanId, userId, authentication.authorities)

        queueInteractionService.takeNext(queuePlanId)
    }
}