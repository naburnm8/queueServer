package ru.naburnm8.queueserver.discipline.response

import ru.naburnm8.queueserver.discipline.entity.WorkType

data class WorkTypesResponse(
    val workTypes: List<WorkType>,
)
