package ru.naburnm8.queueserver.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.profile.entity.Student
import java.util.UUID

interface StudentRepository: JpaRepository<Student, UUID> {
}