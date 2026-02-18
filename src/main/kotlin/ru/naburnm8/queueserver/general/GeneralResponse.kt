package ru.naburnm8.queueserver.general

import java.time.Instant

data class GeneralResponse(
    val message: String,
    val timestamp: Instant = Instant.now(),
)
