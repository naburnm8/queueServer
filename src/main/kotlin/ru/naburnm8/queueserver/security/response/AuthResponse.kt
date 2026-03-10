package ru.naburnm8.queueserver.security.response

import ru.naburnm8.queueserver.security.RoleName


data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val userRole: RoleName
)