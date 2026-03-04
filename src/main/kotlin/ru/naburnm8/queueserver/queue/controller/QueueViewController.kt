package ru.naburnm8.queueserver.queue.controller

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.queue.data.DataMapper
import ru.naburnm8.queueserver.queue.response.QueueSnapshotResponse
import ru.naburnm8.queueserver.queue.service.QueueAccessService
import ru.naburnm8.queueserver.queue.service.QueueRuntimeService
import java.util.UUID

@RestController
class QueueViewController (
    private val queueRuntimeService: QueueRuntimeService,
    private val queueAccessService: QueueAccessService,
) {

    @GetMapping("/queuePlans/{queuePlanId}/view")
    fun view(@PathVariable queuePlanId: UUID, @RequestParam(required = false, defaultValue = "false") force: Boolean, authentication: Authentication): QueueSnapshotResponse {
        val userId = UUID.fromString((authentication.principal as Jwt).subject)

        val granted = queueAccessService.check(queuePlanId, userId, authentication.authorities)

        return if (granted == QueueAccessService.TypeOfGrantedAuthority.QCONSUMER) DataMapper.map(queueRuntimeService.getOrBuild(queuePlanId))
        else {
            if (force) DataMapper.map(queueRuntimeService.refresh(queuePlanId))
            else DataMapper.map(queueRuntimeService.getOrBuild(queuePlanId))
        }
    }
}