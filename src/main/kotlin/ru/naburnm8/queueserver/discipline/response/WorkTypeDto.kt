package ru.naburnm8.queueserver.discipline.response

import java.util.UUID

data class WorkTypeDto(
    val id: UUID,
    val name: String,
    val estimatedTimeMinutes: Int,
)
