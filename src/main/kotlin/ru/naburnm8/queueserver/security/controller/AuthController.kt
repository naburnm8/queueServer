package ru.naburnm8.queueserver.security.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.security.request.LoginRequest
import ru.naburnm8.queueserver.security.request.RegisterRequest
import ru.naburnm8.queueserver.security.response.AuthResponse
import ru.naburnm8.queueserver.security.response.UserResponse
import ru.naburnm8.queueserver.security.service.AuthService
import ru.naburnm8.queueserver.security.service.UserService

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody req: RegisterRequest): UserResponse {
        val user = userService.registerQueueConsumer(req)
        return UserResponse(
            email = user.email,
            roles = user.roles.map { it.name.name }
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): AuthResponse {
        val token = authService.login(req)
        return AuthResponse(accessToken = token)
    }
}