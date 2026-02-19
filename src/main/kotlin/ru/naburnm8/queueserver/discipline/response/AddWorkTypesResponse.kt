package ru.naburnm8.queueserver.discipline.response

import ru.naburnm8.queueserver.discipline.entity.WorkType

data class AddWorkTypesResponse(
    val addedTypes: List<WorkType>,
)
