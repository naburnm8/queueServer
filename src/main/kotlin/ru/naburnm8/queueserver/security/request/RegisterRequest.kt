package ru.naburnm8.queueserver.security.request

data class RegisterRequest (
    val email: String,
    val password: String,
)