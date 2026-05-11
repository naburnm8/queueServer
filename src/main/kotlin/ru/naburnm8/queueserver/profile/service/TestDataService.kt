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
        val passwordHash = passwordEncoder.encode(password) ?: throw IllegalStateException("Failed to hash the password")

        val users = mutableListOf<User>()
        val credentials = mutableListOf<TestStudentResponse>()

        repeat(count) { i ->
            val index = i + 1
            val suffix = UUID.randomUUID().toString().take(8)
            val email = "load_student_${index}_$suffix@test.local"

            users += User(
                email = email,
                passwordHash = passwordHash,
                isEnabled = true,
                roles = mutableSetOf(role)
            )
        }

        val savedUsers = userRepository.saveAll(users)

        val students = savedUsers.mapIndexed { i, user ->
            val index = i + 1

            Student(
                user = user,
                firstName = "Load",
                lastName = "Student$index",
                patronymic = null,
                academicGroup = "LOAD-TEST",
                telegram = "@load_student_$index",
                avatarUrl = null
            )
        }

        val savedStudents = studentRepository.saveAll(students)

        savedStudents.forEachIndexed { i, student ->
            credentials += TestStudentResponse(
                email = savedUsers[i].email,
                password = password,
                studentId = student.userId!!
            )
        }

        return credentials
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