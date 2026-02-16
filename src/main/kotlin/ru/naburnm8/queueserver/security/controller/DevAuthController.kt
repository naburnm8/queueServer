package ru.naburnm8.queueserver.security.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.security.request.RegisterRequest
import ru.naburnm8.queueserver.security.response.UserResponse
import ru.naburnm8.queueserver.security.service.UserService

@Profile("dev")
@RestController
@RequestMapping("/api/dev")
class DevAuthController (
    private val userService: UserService,
) {
    @PostMapping("/create-admin")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAdmin(@RequestBody req: RegisterRequest): UserResponse {
        val user = userService.createAdmin(req)
        return UserResponse(
            email = user.email,
            roles = user.roles.map {it.name.name}
        )
    }
}