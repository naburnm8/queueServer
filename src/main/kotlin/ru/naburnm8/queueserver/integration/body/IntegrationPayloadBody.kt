package ru.naburnm8.queueserver.integration.body

data class IntegrationPayloadBody(
    val baseUrl: String,
    val loginUrl: String,
    val logoutUrl: String,
    val refreshUrl: String,
    val registerUrl: String,
    val disciplinesUrl: String,
    val metricsUrl: String,
)
