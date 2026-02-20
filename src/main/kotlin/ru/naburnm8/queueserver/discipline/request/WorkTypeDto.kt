package ru.naburnm8.queueserver.discipline.request

import java.util.UUID

data class WorkTypeDto(
    val id: UUID? = null,
    val name: String,
    val estimatedTimeMinutes: Int,
)
