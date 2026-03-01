package ru.naburnm8.queueserver.queueRule.body


import java.time.OffsetDateTime

data class TimestampBonusRuleBody(
    val begin: OffsetDateTime,
    val end: OffsetDateTime,
    val bonus: Double
)
