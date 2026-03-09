package ru.naburnm8.queueserver.integration.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import ru.naburnm8.queueserver.integration.body.IntegrationPayloadBody
import ru.naburnm8.queueserver.integration.repository.IntegrationRepository
import ru.naburnm8.queueserver.integration.response.IntegrationResponse
import ru.naburnm8.queueserver.integration.service.IntegrationService
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@RestController
@RequestMapping("/api/auth/integration")
class IntegrationController (
    private val integrationService: IntegrationService,
    private val integrationRepository: IntegrationRepository,
    private val objectMapper: ObjectMapper

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