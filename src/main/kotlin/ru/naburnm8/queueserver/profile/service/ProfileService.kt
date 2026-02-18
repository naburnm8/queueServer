package ru.naburnm8.queueserver.profile.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import ru.naburnm8.queueserver.profile.request.RegisterTeacherRequest
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.service.UserService

@Service
class ProfileService (
    private val studentRepository: StudentRepository,
    private val userService: UserService,
    private val teacherRepository: TeacherRepository,
) {
    @Transactional
    fun registerStudent(req: RegisterStudentRequest): Student {
        val user = userService.createUser(req.email, req.password, RoleName.ROLE_QCONSUMER)

        val student = Student(
            user = user,
            firstName = req.firstName.trim(),
            lastName = req.lastName.trim(),
            patronymic = req.patronymic?.trim(),
            academicGroup = req.academicGroup.trim(),
            telegram = req.telegram?.trim()?.removePrefix("@"),
            avatarUrl = req.avatarUrl?.trim()
        )

        return studentRepository.save(student)
    }

    @Transactional
    fun registerTeacher(req: RegisterTeacherRequest): Teacher {
        val user = userService.createUser(req.email, req.password, RoleName.ROLE_QOPERATOR)

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
}