package ru.naburnm8.queueserver.security.request



data class LoginRequest (
    val email: String,
    val password: String
)