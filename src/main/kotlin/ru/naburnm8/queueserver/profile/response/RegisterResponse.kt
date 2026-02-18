package ru.naburnm8.queueserver.profile.response

import ru.naburnm8.queueserver.security.RoleName

data class RegisterResponse(
    val lastName: String,
    val email: String,
    val role: RoleName
)
