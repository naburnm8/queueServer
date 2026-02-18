package ru.naburnm8.queueserver.profile.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

interface TeacherRepository: JpaRepository<Teacher, UUID> {

}