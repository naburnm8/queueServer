package ru.naburnm8.queueserver.profile.transporter


data class UpdateProfileTransporter(
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val telegram: String?,
    val avatarUrl: String?,
    val multifield: String,
    val multifieldType: ProfileMultifieldType,

)
