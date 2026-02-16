package ru.naburnm8.queueserver.security.response

data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)