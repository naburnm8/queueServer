package ru.naburnm8.queueserver.adminPanel.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateIntegrationForm
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateStudentForm
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateTeacherForm
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.integration.body.IntegrationPayloadBody
import ru.naburnm8.queueserver.integration.entity.Integration
import ru.naburnm8.queueserver.integration.repository.IntegrationRepository
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RoleRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Service
class AdminService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val integrationRepository: IntegrationRepository,
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper
) {
    fun getAllUsers(): List<User> = userRepository.findAllWithRoles()

    fun getAllIntegrations(): List<Integration> = integrationRepository.findAll()

    @Transactional
    fun createStudent(form: AdminCreateStudentForm) {
        val email = form.email.trim().lowercase()
        require(userRepository.findByEmail(email) == null) { "${InnerExceptionCode.USER_ALREADY_EXISTS}" }

        val role = roleRepository.findByName(RoleName.ROLE_QCONSUMER)
            ?: throw IllegalStateException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val user = userRepository.save(
            User(
                email = email,
                passwordHash = passwordEncoder.encode(form.password) ?: throw IllegalStateException("${InnerExceptionCode.SCHEMA_CORRUPTION}"),
                isEnabled = true,
                roles = mutableSetOf(role)
            )
        )

        val student = Student(
            user = user,
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            patronymic = form.patronymic?.trim(),
            academicGroup = form.academicGroup.trim(),
            telegram = form.telegram?.trim(),
            avatarUrl = form.avatarUrl?.trim()
        )

        studentRepository.save(student)
    }

    @Transactional
    fun createTeacher(form: AdminCreateTeacherForm) {
        val email = form.email.trim().lowercase()
        require(userRepository.findByEmail(email) == null) { "${InnerExceptionCode.USER_ALREADY_EXISTS}" }

        val role = roleRepository.findByName(RoleName.ROLE_QOPERATOR)
            ?: throw IllegalStateException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        val user = userRepository.save(
            User(
                email = email,
                passwordHash = passwordEncoder.encode(form.password) ?: throw IllegalStateException("${InnerExceptionCode.SCHEMA_CORRUPTION}"),
                isEnabled = true,
                roles = mutableSetOf(role)
            )
        )

        val teacher = Teacher(
            user = user,
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            patronymic = form.patronymic?.trim(),
            department = form.department.trim(),
            telegram = form.telegram?.trim(),
            avatarUrl = form.avatarUrl?.trim()
        )

        teacherRepository.save(teacher)
    }

    @Transactional
    fun deleteUser(userId: UUID) {
        studentRepository.findById(userId).ifPresent { studentRepository.delete(it) }
        teacherRepository.findById(userId).ifPresent { teacherRepository.delete(it) }
        userRepository.deleteById(userId)
    }

    @Transactional
    fun createIntegration(form: AdminCreateIntegrationForm) {
        val newBody = IntegrationPayloadBody(
            baseUrl = form.baseUrl,
            registerUrl = form.registerUrl,
            loginUrl = "",
            logoutUrl = "",
            refreshUrl = "",
            disciplinesUrl = "",
            metricsUrl = ""
        )

        val integration = Integration(
            name = form.name.trim(),
            payload = objectMapper.writeValueAsString(newBody),
        )
        integrationRepository.save(integration)
    }

    @Transactional
    fun deleteIntegration(id: UUID) {
        integrationRepository.deleteById(id)
    }
}