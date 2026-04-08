package ru.naburnm8.queueserver.integration.response

import ru.naburnm8.queueserver.profile.transporter.ProfileMultifieldType
import java.util.UUID

data class IntegrationRegistrationResponse(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String?,
    val multifield: String,
    val multifieldType: ProfileMultifieldType
)
