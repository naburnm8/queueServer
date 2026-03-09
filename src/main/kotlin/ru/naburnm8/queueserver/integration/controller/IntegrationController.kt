package ru.naburnm8.queueserver.integration.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.integration.response.IntegrationResponse
import ru.naburnm8.queueserver.integration.service.IntegrationService
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import java.util.UUID

@RestController
@RequestMapping("/api/auth/integration")
class IntegrationController (
    private val integrationService: IntegrationService,

) {

    @GetMapping
    fun getAllIntegrations(): List<IntegrationResponse> {
        return integrationService.findAll().map {
            IntegrationResponse(
                id = it.id,
                name = it.name,
                payload = it.payload
            )
        }
    }

    @PostMapping("/{id}/register")
    fun registerStudent(@PathVariable id: UUID, @RequestBody req: RegisterStudentRequest): UUID {
        integrationService.registerStudentViaIntegration(id, req)
        return id
    }

}