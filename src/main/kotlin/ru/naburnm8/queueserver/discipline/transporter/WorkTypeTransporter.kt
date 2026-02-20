package ru.naburnm8.queueserver.discipline.transporter

import java.util.UUID

data class WorkTypeTransporter(
    val id: UUID? = null,
    val name: String,
    val estimatedTimeMinutes: Int,
)
