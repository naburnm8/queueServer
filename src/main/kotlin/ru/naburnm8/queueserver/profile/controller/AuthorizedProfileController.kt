package ru.naburnm8.queueserver.profile.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.profile.response.StudentDto
import ru.naburnm8.queueserver.profile.response.StudentResponse
import ru.naburnm8.queueserver.profile.response.TeacherDto
import ru.naburnm8.queueserver.profile.response.TeacherResponse
import ru.naburnm8.queueserver.profile.service.ProfileService

@RestController
@RequestMapping("/api/profile")
class AuthorizedProfileController (
    private val profileService: ProfileService,
) {
    @PostMapping("/email/teacher")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getTeacherByEmail(@RequestBody email: String): TeacherResponse {
        val preparedEmail = email.replace("\"", "")

        val teacher = profileService.getTeacherByEmail(preparedEmail)
        return TeacherResponse(
            id = teacher.id,
            firstName = teacher.firstName,
            lastName = teacher.lastName,
            department = teacher.department,
            telegram = teacher.telegram ?: "",
            avatarUrl = teacher.avatarUrl ?: "",
            patronymic = teacher.patronymic ?: "",
        )
    }

    @PostMapping("/email/student")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getStudentByEmail(@RequestBody email: String): StudentResponse {
        val student = profileService.getStudentByEmail(email)
        return StudentResponse(
            id = student.id,
            firstName = student.firstName,
            lastName = student.lastName,
            patronymic = student.patronymic ?: "",
            academicGroup = student.academicGroup,
            telegram = student.telegram ?: "",
            avatarUrl = student.avatarUrl ?: "",
        )
    }


    @GetMapping("/groups")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getAllDistinctGroups(): List<String> {
        return profileService.getAllDistinctGroups()
    }

    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getAllDistinctDepartments(): List<String> {
        return profileService.getAllDistinctDepartments()
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getAllTeachers(@RequestParam(required = false) department: String): List<TeacherResponse> {
        return profileService.getAllOrByDepartmentTeachers(department).map {
            TeacherResponse(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                department = it.department,
                telegram = it.telegram ?: "",
                avatarUrl = it.avatarUrl ?: "",
                patronymic = it.patronymic ?: "",
            )
        }
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ROLE_QOPERATOR')")
    fun getAllStudents(@RequestParam(required = false) group: String): List<StudentResponse> {
        return profileService.getAllOrByGroupStudents(group).map {
            StudentResponse(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                patronymic = it.patronymic ?: "",
                academicGroup = it.academicGroup,
                telegram = it.telegram ?: "",
                avatarUrl = it.avatarUrl ?: "",
            )
        }
    }

}