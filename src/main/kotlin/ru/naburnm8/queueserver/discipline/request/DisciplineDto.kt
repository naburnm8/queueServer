package ru.naburnm8.queueserver.discipline.request

import java.util.UUID

data class DisciplineDto(
    val id: UUID? = null,
    val name: String,
)
