package ru.naburnm8.queueserver.discipline.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.discipline.entity.Discipline
import java.util.UUID

interface DisciplineRepository: JpaRepository<Discipline, UUID> {

    @Query("SELECT DISTINCT d FROM Discipline d JOIN d.owners o WHERE o.userId = :ownerId ORDER BY d.name")
    fun findAllByOwnerId(ownerId: UUID): List<Discipline>
}