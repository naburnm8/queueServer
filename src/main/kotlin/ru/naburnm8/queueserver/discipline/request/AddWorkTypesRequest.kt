package ru.naburnm8.queueserver.discipline.request

import java.util.UUID


data class AddWorkTypesRequest(
    val disciplineId: UUID,
    val workTypes: List<WorkTypeDto>
)
