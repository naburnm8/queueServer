package ru.naburnm8.queueserver.discipline.request

import java.util.UUID

data class WorkTypeDto(
    val name: String,
    val estimatedTimeMinutes: Int,
)


data class AddWorkTypesRequest(
    val disciplineId: UUID,
    val workTypes: List<WorkTypeDto>
)
