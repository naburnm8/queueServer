package ru.naburnm8.queueserver.discipline.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.discipline.request.AddWorkTypesRequest
import ru.naburnm8.queueserver.discipline.request.CreateNewDisciplineRequest
import ru.naburnm8.queueserver.discipline.request.DeleteRequest
import ru.naburnm8.queueserver.discipline.request.UpdateDisciplinesRequest
import ru.naburnm8.queueserver.discipline.request.UpdateWorkTypesRequest
import ru.naburnm8.queueserver.discipline.response.DisciplineDto
import ru.naburnm8.queueserver.discipline.response.DisciplinesResponse
import ru.naburnm8.queueserver.discipline.response.WorkTypeDto
import ru.naburnm8.queueserver.discipline.response.WorkTypesResponse
import ru.naburnm8.queueserver.discipline.service.DisciplineService
import ru.naburnm8.queueserver.discipline.transporter.DisciplineTransporter
import ru.naburnm8.queueserver.discipline.transporter.TransporterMapper
import ru.naburnm8.queueserver.discipline.transporter.WorkTypeTransporter
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.JwtUtils
import java.util.UUID

@RestController
@RequestMapping("/api/discipline")
class DisciplineController (
    private val disciplineService: DisciplineService,
) {

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getMyDisciplines(): DisciplinesResponse {
        return DisciplinesResponse(
            disciplines = disciplineService.getDisciplines(JwtUtils.currentAuthenticatedUserId()).map {discipline -> DisciplineDto(id = discipline.id, name = discipline.name)}
        )
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun createDiscipline(@RequestBody req: CreateNewDisciplineRequest): DisciplinesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val discipline = disciplineService.createNewDiscipline(TransporterMapper.map(req, subject))
            return DisciplinesResponse(
            disciplines = listOf(DisciplineDto(discipline.id, discipline.name))
        )
    }

    @GetMapping("/{id}/workTypes")
    fun getWorkTypesById(@PathVariable id: UUID): WorkTypesResponse {
        return WorkTypesResponse(
            workTypes = disciplineService.getWorkTypesByDiscipline(id).map {workType -> WorkTypeDto(id = workType.id, name = workType.name, estimatedTimeMinutes = workType.estimatedTimeMinutes) }
        )
    }

    @PostMapping("/workTypes")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun addWorkTypes(@RequestBody req: AddWorkTypesRequest): WorkTypesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        return WorkTypesResponse(
            workTypes = disciplineService.addWorkTypes(TransporterMapper.map(req, subject)).map {workType -> WorkTypeDto(id = workType.id, name = workType.name, estimatedTimeMinutes = workType.estimatedTimeMinutes)}
        )
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun deleteDisciplines(@RequestBody req: DeleteRequest) {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        disciplineService.deleteDisciplines(req.ids, subject)
    }

    @DeleteMapping("/workTypes")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun deleteWorkTypes(@RequestBody req: DeleteRequest) {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        disciplineService.deleteWorkTypes(req.ids, subject)
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun updateDisciplines(@RequestBody req: UpdateDisciplinesRequest): DisciplinesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val out = disciplineService.updateDisciplines(req.newDisciplines.map {dto -> DisciplineTransporter(id = dto.id ?: throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}"), name = dto.name)}, subject)
        return DisciplinesResponse(out.map {transporter -> DisciplineDto(transporter.id, transporter.name)})
    }

    @PutMapping("/workTypes")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun updateWorkTypes(@RequestBody req: UpdateWorkTypesRequest): WorkTypesResponse {
        val subject = JwtUtils.currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val out = disciplineService.updateWorkTypes(req.updated.map {dto -> WorkTypeTransporter(dto.id, dto.name, dto.estimatedTimeMinutes) }, subject)
        return WorkTypesResponse(
            out.map {transporter -> WorkTypeDto(transporter.id ?: throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}"), transporter.name, transporter.estimatedTimeMinutes)}
        )
    }


}