package ru.naburnm8.queueserver.security.response

data class UserResponse (
    val email: String,
    val roles: List<String>,
)