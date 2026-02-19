package ru.naburnm8.queueserver.discipline.service

import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.discipline.entity.WorkType
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.repository.WorkTypeRepository
import ru.naburnm8.queueserver.discipline.request.AddWorkTypesRequest
import ru.naburnm8.queueserver.discipline.request.CreateNewDisciplineRequest
import ru.naburnm8.queueserver.discipline.transporter.AddWorkTypesTransporter
import ru.naburnm8.queueserver.discipline.transporter.CreateNewDisciplineTransporter
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import java.util.UUID

@Service
class DisciplineService (
    private val disciplineRepository: DisciplineRepository,
    private val workTypeRepository: WorkTypeRepository,
    private val teacherRepository: TeacherRepository,
) {

    fun createNewDiscipline(request: CreateNewDisciplineTransporter): Discipline {
        val owner = teacherRepository.findById(request.identity)

        if (owner.isEmpty) {
            throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        }

        val newDiscipline = Discipline(
            name = request.name,
            owners = mutableSetOf(owner.get())
        )

        return disciplineRepository.save(newDiscipline)
    }

    fun addWorkTypes(request: AddWorkTypesTransporter): List<WorkType> {
        val discipline = disciplineRepository.findById(request.disciplineId)
        val requester = teacherRepository.findByUserId(request.identity)

        if (discipline.isEmpty) {
            throw RuntimeException(InnerExceptionCode.NO_SUCH_DISCIPLINE.toString())
        }

        if (requester in discipline.get().owners) {
            val transformed = request.workTypes.map {
                    workType -> WorkType(
                    name = workType.name,
                    estimatedTimeMinutes = workType.estimatedTimeMinutes,
                    discipline = discipline.get()
                )
            }
            workTypeRepository.saveAll(transformed)
            return transformed
        } else {
            throw RuntimeException(InnerExceptionCode.DISCIPLINE_NOT_OWNED.toString())
        }
    }

    fun getWorkTypesByDiscipline(disciplineId: UUID): List<WorkType> {
        return workTypeRepository.findByDisciplineId(disciplineId)
    }

    fun getDisciplines(ownerId: UUID? = null): List<Discipline> {
        return if (ownerId == null) {
            disciplineRepository.findAll()
        } else {
            disciplineRepository.findAllByOwnerId(ownerId)
        }

    }
}