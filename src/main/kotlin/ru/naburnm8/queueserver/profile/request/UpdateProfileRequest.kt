package ru.naburnm8.queueserver.profile.request

import ru.naburnm8.queueserver.profile.transporter.ProfileMultifieldType

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val telegram: String?,
    val avatarUrl: String?,
    val multifield: String,
    val multifieldType: ProfileMultifieldType,
)
