package ru.naburnm8.queueserver.discipline.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.discipline.entity.Discipline
import java.util.UUID

interface DisciplineRepository: JpaRepository<Discipline, UUID> {
}