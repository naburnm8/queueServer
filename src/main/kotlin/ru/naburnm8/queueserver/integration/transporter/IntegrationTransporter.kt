package ru.naburnm8.queueserver.integration.transporter

import tools.jackson.databind.JsonNode
import java.util.UUID

data class IntegrationTransporter(
    val id: UUID,
    val name: String,
    val payload: JsonNode,
)
