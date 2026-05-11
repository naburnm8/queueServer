package ru.naburnm8.queueserver.profile.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.response.TestStudentResponse
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RoleRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import java.util.UUID

@Service
class TestDataService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val studentRepository: StudentRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createStudents(count: Int): List<TestStudentResponse> {
        val role = roleRepository.findByName(RoleName.ROLE_QCONSUMER)
            ?: throw IllegalStateException("ROLE_QCONSUMER not found")

        val password = "123456"

        return (1..count).map { index ->
            val suffix = UUID.randomUUID().toString().take(8)
            val email = "load_student_${index}_$suffix@test.local"

            val user = userRepository.save(
                User(
                    email = email,
                    passwordHash = passwordEncoder.encode(password) ?: "",
                    isEnabled = true,
                    roles = mutableSetOf(role)
                )
            )

            val student = studentRepository.save(
                Student(
                    user = user,
                    firstName = "Load",
                    lastName = "Student$index",
                    patronymic = null,
                    academicGroup = "LOAD-TEST",
                    telegram = "@load_student_$index",
                    avatarUrl = null
                )
            )

            TestStudentResponse(
                email = email,
                password = password,
                studentId = student.userId!!
            )
        }
    }

    @Transactional
    fun deleteLoadStudents() {
        val users = userRepository.findAll()
            .filter { it.email.startsWith("load_student_") }

        users.forEach { user ->
            userRepository.delete(user)
        }
    }
}