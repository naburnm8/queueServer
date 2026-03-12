package ru.naburnm8.queueserver.profile.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.profile.request.UpdateProfileRequest
import ru.naburnm8.queueserver.profile.response.StudentResponse
import ru.naburnm8.queueserver.profile.response.TeacherResponse
import ru.naburnm8.queueserver.profile.response.UpdateProfileResponse
import ru.naburnm8.queueserver.profile.service.ProfileService
import ru.naburnm8.queueserver.profile.transporter.UpdateProfileTransporter
import ru.naburnm8.queueserver.security.JwtUtils

@RestController
@RequestMapping("/api/profile/me")
class MeController (
    private val profileService: ProfileService
) {
    @PutMapping()
    fun updateMe(@RequestBody req: UpdateProfileRequest): UpdateProfileResponse {
        val subject = JwtUtils.getSubject()
        val updated = profileService.updateMe(
            UpdateProfileTransporter(
                firstName = req.firstName,
                lastName = req.lastName,
                patronymic = req.patronymic,
                telegram = req.telegram,
                avatarUrl = req.avatarUrl,
                multifield = req.multifield,
                multifieldType = req.multifieldType,
            ),
            subject)

        return UpdateProfileResponse(
            firstName = updated.firstName,
            lastName = updated.lastName,
            patronymic = updated.patronymic,
            telegram = updated.telegram,
            avatarUrl = updated.avatarUrl,
            multifield = updated.multifield,
            multifieldType = updated.multifieldType,
        )
    }

    @GetMapping("/student")
    fun getMeStudent(): StudentResponse {
        val subject = JwtUtils.getSubject()
        val me = profileService.getMeStudent(subject)

        return StudentResponse(
            id = me.id,
            lastName = me.lastName,
            firstName = me.firstName,
            patronymic = me.patronymic,
            academicGroup = me.academicGroup,
            telegram = me.telegram,
            avatarUrl = me.avatarUrl
        )
    }

    @GetMapping("/teacher")
    fun getMeTeacher(): TeacherResponse {
        val subject = JwtUtils.getSubject()
        val me = profileService.getMeTeacher(subject)

        return TeacherResponse(
            id = me.id,
            lastName = me.lastName,
            firstName = me.firstName,
            patronymic = me.patronymic,
            department = me.department,
            telegram = me.telegram,
            avatarUrl = me.avatarUrl
        )
    }

}