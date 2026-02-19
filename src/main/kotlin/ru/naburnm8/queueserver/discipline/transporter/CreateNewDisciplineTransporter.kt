package ru.naburnm8.queueserver.discipline.transporter

import java.util.UUID

data class CreateNewDisciplineTransporter (
    val identity: UUID,
    val name: String,
)