package ru.naburnm8.queueserver.integration.response

import tools.jackson.databind.JsonNode
import java.util.UUID

data class IntegrationResponse(
    val id: UUID,
    val name: String,
    val payload: JsonNode,
)
