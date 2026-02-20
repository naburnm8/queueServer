package ru.naburnm8.queueserver.discipline.response

import ru.naburnm8.queueserver.discipline.entity.Discipline

data class DisciplinesResponse(
    val disciplines: List<DisciplineDto>
)
