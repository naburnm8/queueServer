package ru.naburnm8.queueserver.profile.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.profile.request.RegisterStudentRequest
import ru.naburnm8.queueserver.profile.request.RegisterTeacherRequest
import ru.naburnm8.queueserver.profile.response.RegisterResponse
import ru.naburnm8.queueserver.profile.service.ProfileService
import ru.naburnm8.queueserver.security.RoleName

@RestController
@RequestMapping("/api/auth/register")
class ProfileController (
    private val profileService: ProfileService
) {
    @PostMapping("/student")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerStudent(@RequestBody req: RegisterStudentRequest): RegisterResponse {
        val student = profileService.registerStudent(req)
        return RegisterResponse(
            lastName = student.lastName,
            email = student.user.email,
            role = RoleName.ROLE_QCONSUMER
        )
    }

    @Profile("dev")
    @PostMapping("/teacher")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerTeacher(@RequestBody req: RegisterTeacherRequest): RegisterResponse {
        val teacher = profileService.registerTeacher(req)
        return RegisterResponse(
            lastName = teacher.lastName,
            email = teacher.user.email,
            role = RoleName.ROLE_QOPERATOR
        )
    }
}