package ru.naburnm8.queueserver.queueRule.body

data class IdentifierBonusRuleBody(
    val field: IdentifierField,
    val values: List<String>,
    val bonus: Double
)
