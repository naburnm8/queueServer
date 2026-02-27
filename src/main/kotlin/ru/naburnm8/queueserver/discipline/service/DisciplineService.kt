package ru.naburnm8.queueserver.discipline.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.discipline.entity.WorkType
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.repository.WorkTypeRepository
import ru.naburnm8.queueserver.discipline.request.AddWorkTypesRequest
import ru.naburnm8.queueserver.discipline.request.CreateNewDisciplineRequest
import ru.naburnm8.queueserver.discipline.transporter.AddWorkTypesTransporter
import ru.naburnm8.queueserver.discipline.transporter.CreateNewDisciplineTransporter
import ru.naburnm8.queueserver.discipline.transporter.DisciplineTransporter
import ru.naburnm8.queueserver.discipline.transporter.WorkTypeTransporter
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import java.util.UUID

@Service
class DisciplineService (
    private val disciplineRepository: DisciplineRepository,
    private val workTypeRepository: WorkTypeRepository,
    private val teacherRepository: TeacherRepository,
    private val ownershipService: OwnershipService
) {
    @Transactional
    fun addOwnersToDiscipline(requesterId: UUID, idsToAdd: List<UUID>, disciplineId: UUID) {
        ownershipService.checkOwnership(requesterId, disciplineId)
        val discipline = disciplineRepository.findById(disciplineId).get()

        val teachersToAdd = mutableListOf<Teacher>()

        for (id in idsToAdd) {
            val teacher = teacherRepository.findById(id)
            if (teacher.isEmpty) throw RuntimeException("${InnerExceptionCode.SCHEMA_CORRUPTION}")
            teachersToAdd.add(teacher.get())
        }

        discipline.owners.addAll(teachersToAdd)
        disciplineRepository.save(discipline)

    }

    @Transactional
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

    @Transactional
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

    @Transactional
    fun getWorkTypesByDiscipline(disciplineId: UUID): List<WorkType> {
        return workTypeRepository.findByDisciplineId(disciplineId)
    }

    @Transactional
    fun getDisciplines(ownerId: UUID? = null): List<Discipline> {
        return if (ownerId == null) {
            disciplineRepository.findAll()
        } else {
            disciplineRepository.findAllByOwnerId(ownerId)
        }
    }
    @Transactional
    fun deleteDiscipline(disciplineId: UUID, identity: UUID) {
        val requester = teacherRepository.findByUserId(identity)
        val discipline = disciplineRepository.findById(disciplineId)
        if (discipline.isEmpty) throw RuntimeException(InnerExceptionCode.NO_SUCH_DISCIPLINE.toString())
        if (requester !in discipline.get().owners) throw RuntimeException(InnerExceptionCode.DISCIPLINE_NOT_OWNED.toString())

        disciplineRepository.deleteById(disciplineId)
    }
    @Transactional
    fun deleteDisciplines(ids: List<UUID>, identity: UUID) {
        for (id in ids) {
            deleteDiscipline(id, identity)
        }
    }
    @Transactional
    fun deleteWorkType(workTypeId: UUID, identity: UUID) {
        val requester = teacherRepository.findByUserId(identity) ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val workType = workTypeRepository.findById(workTypeId)
        if (workType.isEmpty) throw RuntimeException(InnerExceptionCode.NO_SUCH_WORK_TYPE.toString())
        val discipline = workType.get().discipline
        if (requester !in discipline.owners) throw RuntimeException(InnerExceptionCode.DISCIPLINE_NOT_OWNED.toString())
        workTypeRepository.deleteById(workTypeId)
    }

    @Transactional
    fun deleteWorkTypes(workTypesIds: List<UUID>, identity: UUID) {
        for (id in workTypesIds) {
            deleteWorkType(id, identity)
        }
    }
    @Transactional
    fun updateDiscipline(updated: DisciplineTransporter, identity: UUID): DisciplineTransporter {
        val requester = teacherRepository.findByUserId(identity) ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val disciplineInDB = disciplineRepository.findById(updated.id)
        if (disciplineInDB.isEmpty) throw RuntimeException(InnerExceptionCode.NO_SUCH_DISCIPLINE.toString())
        if (requester !in disciplineInDB.get().owners) throw RuntimeException(InnerExceptionCode.DISCIPLINE_NOT_OWNED.toString())

        disciplineInDB.get().name = updated.name

        val newDiscipline = disciplineRepository.save(disciplineInDB.get())

        return DisciplineTransporter(newDiscipline.id, newDiscipline.name)
    }
    @Transactional
    fun updateDisciplines(updated: List<DisciplineTransporter>, identity: UUID): List<DisciplineTransporter> {
        val out = mutableListOf<DisciplineTransporter>()
        for (discipline in updated) {
            out.add(updateDiscipline(discipline, identity))
        }
        return out
    }

    @Transactional
    fun updateWorkType(workTypeIn: WorkTypeTransporter, identity: UUID): WorkTypeTransporter {
        if (workTypeIn.id == null) throw RuntimeException(InnerExceptionCode.SCHEMA_CORRUPTION.toString())

        val requester = teacherRepository.findByUserId(identity) ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        val workTypeInDB = workTypeRepository.findById(workTypeIn.id)
        if (workTypeInDB.isEmpty) throw RuntimeException(InnerExceptionCode.NO_SUCH_WORK_TYPE.toString())
        val workType = workTypeInDB.get()
        val discipline = workType.discipline
        if (requester !in discipline.owners) throw RuntimeException(InnerExceptionCode.DISCIPLINE_NOT_OWNED.toString())

        workType.name = workTypeIn.name
        workType.estimatedTimeMinutes = workTypeIn.estimatedTimeMinutes

       val newWorkType = workTypeRepository.save(workType)

        return WorkTypeTransporter(newWorkType.id, newWorkType.name, estimatedTimeMinutes = newWorkType.estimatedTimeMinutes)
    }

    @Transactional
    fun updateWorkTypes(updated: List<WorkTypeTransporter>, identity: UUID): List<WorkTypeTransporter> {
        val out = mutableListOf<WorkTypeTransporter>()
        for (workTypeIn in updated) {
            out.add(updateWorkType(workTypeIn, identity))
        }
        return out
    }
}