package ru.naburnm8.queueserver.discipline.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.discipline.entity.WorkType
import java.util.UUID

interface WorkTypeRepository: JpaRepository<WorkType, UUID> {
    fun findByDisciplineId(disciplineId: UUID): WorkType?
}