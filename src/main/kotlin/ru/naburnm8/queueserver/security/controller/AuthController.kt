package ru.naburnm8.queueserver.security.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.security.request.LoginRequest
import ru.naburnm8.queueserver.security.request.LogoutRequest
import ru.naburnm8.queueserver.security.request.RefreshRequest
import ru.naburnm8.queueserver.security.response.AuthResponse
import ru.naburnm8.queueserver.security.service.AuthService


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest, http: HttpServletRequest): AuthResponse {
        val tokens = authService.login(
            loginRequest = req,
            userAgent = http.getHeader("User-Agent"),
            ip = http.remoteAddr
        )

        return AuthResponse(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody req: RefreshRequest, http: HttpServletRequest): AuthResponse {
        val tokens = authService.refresh(
            refreshTokenValue = req.refreshToken,
            userAgent = http.getHeader("User-Agent"),
            ip = http.remoteAddr
        )

        return AuthResponse(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody req: LogoutRequest) {
        authService.logout(req.refreshToken)
    }
}