package ru.naburnm8.queueserver.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.profile.entity.Student
import java.util.UUID

interface StudentRepository: JpaRepository<Student, UUID> {
    fun findAllByAcademicGroup(academicGroup: String): MutableList<Student>

    @Query("SELECT DISTINCT s.academicGroup FROM Student s ORDER BY s.academicGroup")
    fun findAllDistinctGroups(): List<String>
}