package ru.naburnm8.queueserver.profile.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import ru.naburnm8.queueserver.profile.request.RegisterTeacherRequest
import ru.naburnm8.queueserver.profile.response.StudentDto
import ru.naburnm8.queueserver.profile.transporter.ProfileMultifieldType
import ru.naburnm8.queueserver.profile.transporter.StudentTransporter
import ru.naburnm8.queueserver.profile.transporter.TeacherTransporter
import ru.naburnm8.queueserver.profile.transporter.UpdateProfileTransporter
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.service.UserService
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class ProfileService (
    private val studentRepository: StudentRepository,
    private val userService: UserService,
    private val teacherRepository: TeacherRepository,
) {
    @Transactional
    fun updateMe(updated: UpdateProfileTransporter, requesterId: UUID): UpdateProfileTransporter {
        var existing: Any? = studentRepository.findById(requesterId).getOrNull()


        if (existing == null) {
            existing = teacherRepository.findById(requesterId).getOrNull() ?: throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")
            existing.firstName = updated.firstName
            existing.lastName = updated.lastName
            existing.patronymic = updated.patronymic
            existing.avatarUrl = updated.avatarUrl
            existing.telegram = updated.telegram

            if (updated.multifieldType == ProfileMultifieldType.DEPARTMENT)
                existing.department = updated.multifield

            teacherRepository.save(existing)
        } else {
            existing as Student
            existing.firstName = updated.firstName
            existing.lastName = updated.lastName
            existing.patronymic = updated.patronymic
            existing.avatarUrl = updated.avatarUrl
            existing.telegram = updated.telegram

            if (updated.multifieldType == ProfileMultifieldType.ACADEMIC_GROUP)
                existing.academicGroup = updated.multifield

            studentRepository.save(existing)
        }

        return updated



    }


    @Transactional
    fun getMeStudent(requesterId: UUID): StudentTransporter {
        val found = studentRepository.findById(requesterId).getOrNull() ?: throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")

        return StudentTransporter(
            id = found.userId ?: UUID(0,0),
            firstName = found.firstName,
            lastName = found.lastName,
            patronymic = found.patronymic,
            academicGroup = found.academicGroup,
            telegram = found.telegram,
            avatarUrl = found.avatarUrl
        )

    }

    @Transactional
    fun getMeTeacher(requesterId: UUID): TeacherTransporter {
        val found = teacherRepository.findById(requesterId).getOrNull() ?: throw RuntimeException("${InnerExceptionCode.USER_NOT_FOUND}")

        return TeacherTransporter(
            id = found.userId ?: UUID(0,0),
            firstName = found.firstName,
            lastName = found.lastName,
            patronymic = found.patronymic,
            department = found.department,
            telegram = found.telegram,
            avatarUrl = found.avatarUrl
        )
    }


    @Transactional
    fun registerStudent(req: RegisterStudentRequest, integrationId: UUID? = null, externalUserId: UUID? = null): Student {
        val existingUser = userService.findByEmailOrNull(req.email)
        if (existingUser != null) {
            throw RuntimeException("${InnerExceptionCode.USER_ALREADY_EXISTS}")
        }

        val user = userService.createUser(
            req.email,
            req.password,
            RoleName.ROLE_QCONSUMER,
            integrationId = integrationId,
            externalUserId = externalUserId
        )

        val student = Student(
            user = user,
            firstName = req.firstName.trim(),
            lastName = req.lastName.trim(),
            patronymic = req.patronymic?.trim(),
            academicGroup = req.academicGroup.trim(),
            telegram = req.telegram?.trim()?.removePrefix("@"),
            avatarUrl = req.avatarUrl?.trim(),
        )

        return studentRepository.save(student)
    }

    @Transactional
    fun registerTeacher(req: RegisterTeacherRequest, integrationId: UUID? = null, externalUserId: UUID? = null): Teacher {
        val existingUser = userService.findByEmailOrNull(req.email)
        if (existingUser != null) {
            throw RuntimeException("${InnerExceptionCode.USER_ALREADY_EXISTS}")
        }

        val user = userService.createUser(
            req.email,
            req.password,
            RoleName.ROLE_QOPERATOR,
            integrationId = integrationId,
            externalUserId = externalUserId
        )

        val teacher = Teacher(
            user = user,
            firstName = req.firstName.trim(),
            lastName = req.lastName.trim(),
            patronymic = req.patronymic?.trim(),
            department = req.department.trim(),
            telegram = req.telegram?.trim()?.removePrefix("@"),
            avatarUrl = req.avatarUrl?.trim()
        )

        return teacherRepository.save(teacher)
    }

    @Transactional
    fun getAllOrByGroupStudents(academicGroup: String? = null): List<StudentTransporter> {
        val found = if (academicGroup == null) studentRepository.findAll() else studentRepository.findAllByAcademicGroup(academicGroup)
        return found.map { entity -> StudentTransporter(
            id = entity.userId ?: UUID(0,0),
            firstName = entity.firstName,
            lastName = entity.lastName,
            patronymic = entity.patronymic,
            academicGroup = entity.academicGroup,
            telegram = entity.telegram,
            avatarUrl = entity.avatarUrl
        ) }
    }

    @Transactional
    fun getAllOrByDepartmentTeachers(department: String? = null): List<TeacherTransporter> {
        val found = if (department == null) teacherRepository.findAll() else teacherRepository.findTeachersByDepartment(department)
        return found.map { entity -> TeacherTransporter(
            id = entity.userId ?: UUID(0,0),
            firstName = entity.firstName,
            lastName = entity.lastName,
            patronymic = entity.patronymic,
            department = entity.department,
            telegram = entity.telegram,
            avatarUrl = entity.avatarUrl
        ) }
    }

    @Transactional
    fun getAllDistinctGroups(): List<String> {
        return studentRepository.findAllDistinctGroups()
    }

    @Transactional
    fun getAllDistinctDepartments(): List<String> {
        return teacherRepository.findAllDistinctDepartments()
    }
}