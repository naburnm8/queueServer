package ru.naburnm8.queueserver.discipline.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.discipline.request.AddWorkTypesRequest
import ru.naburnm8.queueserver.discipline.request.CreateNewDisciplineRequest
import ru.naburnm8.queueserver.discipline.response.DisciplinesResponse
import ru.naburnm8.queueserver.discipline.response.WorkTypesResponse
import ru.naburnm8.queueserver.discipline.service.DisciplineService
import ru.naburnm8.queueserver.discipline.transporter.TransporterMapper
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.JwtUtils
import java.util.UUID

@RestController
@RequestMapping("/api/discipline")
class DisciplineController (
    private val disciplineService: DisciplineService,
) {

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR', 'ROLE_ADMIN')")
    fun getMyDisciplines(): DisciplinesResponse {
        return DisciplinesResponse(
            disciplines = disciplineService.getDisciplines(JwtUtils.currentAuthenticatedUserId())
        )
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR', 'ROLE_ADMIN')")
    fun createDiscipline(@RequestBody req: CreateNewDisciplineRequest): DisciplinesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        return DisciplinesResponse(
            disciplines = listOf(disciplineService.createNewDiscipline(TransporterMapper.map(req, subject) ))
        )
    }

    @GetMapping("/{id}/work-types")
    fun getWorkTypesById(@PathVariable id: UUID): WorkTypesResponse {
        return WorkTypesResponse(
            workTypes = disciplineService.getWorkTypesByDiscipline(id)
        )
    }

    @PostMapping("/{id}/workTypes")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR', 'ROLE_ADMIN')")
    fun addWorkTypes(@PathVariable id: UUID, @RequestBody req: AddWorkTypesRequest): WorkTypesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        return WorkTypesResponse(
            workTypes = disciplineService.addWorkTypes(TransporterMapper.map(req, subject))
        )
    }


}