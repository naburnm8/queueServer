package ru.naburnm8.queueserver.profile.response

import ru.naburnm8.queueserver.profile.transporter.ProfileMultifieldType

data class UpdateProfileResponse(
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val telegram: String?,
    val avatarUrl: String?,
    val multifield: String,
    val multifieldType: ProfileMultifieldType,
)
