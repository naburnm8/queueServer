package ru.naburnm8.queueserver.discipline.request

data class UpdateDisciplinesRequest(
    val newDisciplines: List<DisciplineDto>
)
