package ru.naburnm8.queueserver.discipline.transporter

import java.util.UUID



data class AddWorkTypesTransporter (
    val identity: UUID,
    val disciplineId: UUID,
    val workTypes: List<WorkTypeTransporter>
)