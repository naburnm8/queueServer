package ru.naburnm8.queueserver.discipline.service

import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import java.util.UUID

@Service
class OwnershipService (
    private val teacherRepository: TeacherRepository,
    private val disciplineRepository: DisciplineRepository,
) {

    fun checkOwnership(requesterId: UUID, disciplineId: UUID) {
        val teacher = teacherRepository.findById(requesterId)
        if (teacher.isEmpty) throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")

        val discipline = disciplineRepository.findById(disciplineId)
        if (discipline.isEmpty) throw RuntimeException("${InnerExceptionCode.NO_SUCH_DISCIPLINE}")

        val disciplineInDb = discipline.get()

        if (teacher.get() !in disciplineInDb.owners) throw RuntimeException("${InnerExceptionCode.DISCIPLINE_NOT_OWNED}")
    }

}