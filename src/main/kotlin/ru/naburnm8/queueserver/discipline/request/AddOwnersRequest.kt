package ru.naburnm8.queueserver.discipline.request

import java.util.UUID

data class AddOwnersRequest(
    val idsToAdd: List<UUID>,
)
