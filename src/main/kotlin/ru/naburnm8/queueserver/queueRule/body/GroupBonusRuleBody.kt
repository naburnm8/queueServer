package ru.naburnm8.queueserver.queueRule.body

data class GroupBonusRuleBody(
    val groups: List<String>,
    val bonus: Double
)
