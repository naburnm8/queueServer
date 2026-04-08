package ru.naburnm8.queueserver.integration.service

import jakarta.transaction.Transactional
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.integration.body.IntegrationPayloadBody
import ru.naburnm8.queueserver.integration.repository.IntegrationRepository
import ru.naburnm8.queueserver.integration.transporter.IntegrationTransporter
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import ru.naburnm8.queueserver.studentMetrics.service.StudentMetricsService
import ru.naburnm8.queueserver.studentMetrics.transporter.StudentMetricsTransporterIn
import tools.jackson.databind.ObjectMapper
import java.util.UUID
import ru.naburnm8.queueserver.discipline.service.DisciplineService
import ru.naburnm8.queueserver.discipline.transporter.DisciplineTransporter
import ru.naburnm8.queueserver.integration.response.IntegrationRegistrationResponse
import ru.naburnm8.queueserver.profile.request.RegisterTeacherRequest
import ru.naburnm8.queueserver.profile.service.ProfileService

@Service
class IntegrationService (
    private val integrationRepository: IntegrationRepository,
    private val objectMapper: ObjectMapper,
    private val studentMetricsService: StudentMetricsService,
    private val disciplineService: DisciplineService,
    private val profileService: ProfileService,
)  {

    fun findAll(): List<IntegrationTransporter> {
        val found = integrationRepository.findAll()
        val mapped = found.map { integration ->
            val payload = objectMapper.readTree(integration.payload)
            IntegrationTransporter(
                id = integration.id,
                name = integration.name,
                payload = payload
            )
        }
        val mutableMapped = mapped.toMutableList()
        mutableMapped.add(
            IntegrationTransporter(
                id = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                name = "native",
                payload = objectMapper.readTree("")
            )
        )
        return mutableMapped
    }


    fun registerTeacherViaIntegration(id: UUID, req: RegisterTeacherRequest): UUID {
        val integration = integrationRepository.findById(id).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_INTEGRATION}") }
        val payloadBody = objectMapper.readValue(integration.payload, IntegrationPayloadBody::class.java)
        val restTemplate = RestTemplate()
        val response: ResponseEntity<IntegrationRegistrationResponse> = restTemplate.postForEntity(
            payloadBody.baseUrl + payloadBody.registerUrl,
            req,
            IntegrationRegistrationResponse::class.java
        )
        if (response.statusCode != HttpStatus.OK) {
            throw RuntimeException("Registration failed")
        }

        response.body?.id ?: throw RuntimeException("Registration failed")

        val newStudent = RegisterTeacherRequest(
            email = response.body?.email!!,
            password = req.password,
            firstName = response.body?.firstName!!,
            lastName = response.body?.lastName!!,
            patronymic = response.body?.patronymic!!,
            department = response.body?.multifield!!,
            telegram = req.telegram!!,
            avatarUrl = req.avatarUrl,
        )

        profileService.registerTeacher(newStudent)

        return response.body?.id!!
    }


    fun registerStudentViaIntegration(id: UUID, req: RegisterStudentRequest): UUID {
        val integration = integrationRepository.findById(id).orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_INTEGRATION}") }
        val payloadBody = objectMapper.readValue(integration.payload, IntegrationPayloadBody::class.java)
        val restTemplate = RestTemplate()
        val response: ResponseEntity<IntegrationRegistrationResponse> = restTemplate.postForEntity(
            payloadBody.baseUrl + payloadBody.registerUrl,
            req,
            IntegrationRegistrationResponse::class.java
        )
        if (response.statusCode != HttpStatus.OK) {
            throw RuntimeException("Registration failed")
        }

        response.body?.id ?: throw RuntimeException("Registration failed")

        val newStudent = RegisterStudentRequest(
            email = response.body?.email!!,
            password = req.password,
            firstName = response.body?.firstName!!,
            lastName = response.body?.lastName!!,
            patronymic = response.body?.patronymic!!,
            academicGroup = response.body?.multifield!!,
            telegram = req.telegram!!,
            avatarUrl = req.avatarUrl,
        )

        profileService.registerStudent(newStudent)

        return response.body?.id!!
    }

    @Transactional
    fun syncMetricsFromIntegrations() {
        val integrations = findAll().filter { it.id != UUID.fromString("00000000-0000-0000-0000-000000000000") }
        for (integration in integrations) {
            val payloadBody = objectMapper.readValue(integration.payload.toString(), IntegrationPayloadBody::class.java)
            val restTemplate = RestTemplate()
            val typeRef = object : ParameterizedTypeReference<List<StudentMetricsTransporterIn>>() {}
            val response: ResponseEntity<List<StudentMetricsTransporterIn>> = restTemplate.exchange(
                payloadBody.baseUrl + payloadBody.metricsUrl,
                HttpMethod.POST,
                null,
                typeRef
            )
            if (response.statusCode == HttpStatus.OK && response.body != null) {
                response.body!!.forEach { studentMetricsService.upsert(it) }
            }
        }
    }

    @Transactional
    fun syncDisciplinesFromIntegrations() {
        val integrations = findAll().filter { it.id != UUID.fromString("00000000-0000-0000-0000-000000000000") }
        for (integration in integrations) {
            val payloadBody = objectMapper.readValue(integration.payload.toString(), IntegrationPayloadBody::class.java)
            val restTemplate = RestTemplate()
            val typeRef = object : ParameterizedTypeReference<List<DisciplineTransporter>>() {}
            val response: ResponseEntity<List<DisciplineTransporter>> = restTemplate.exchange(
                payloadBody.baseUrl + payloadBody.disciplinesUrl,
                HttpMethod.POST,
                null,
                typeRef
            )
            if (response.statusCode == HttpStatus.OK && response.body != null) {
                response.body!!.forEach { disciplineService.upsert(it) }
            }
        }
    }
}