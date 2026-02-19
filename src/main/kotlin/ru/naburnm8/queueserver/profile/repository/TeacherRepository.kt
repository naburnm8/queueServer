package ru.naburnm8.queueserver.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

interface TeacherRepository: JpaRepository<Teacher, UUID> {

    @Query("SELECT DISTINCT t.department FROM Teacher t ORDER BY t.department")
    fun findAllDistinctDepartments(): List<String>

    fun findByUserId(userId: UUID): Teacher?

    @Query("SELECT u.email FROM Teacher t join t.user u where u.email = :userEmail")
    fun findTeacherByUserEmail(userEmail: String): Teacher?
}