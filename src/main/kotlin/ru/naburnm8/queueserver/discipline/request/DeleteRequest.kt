package ru.naburnm8.queueserver.discipline.request

import java.util.UUID

data class DeleteRequest(
    val ids: List<UUID>
)
