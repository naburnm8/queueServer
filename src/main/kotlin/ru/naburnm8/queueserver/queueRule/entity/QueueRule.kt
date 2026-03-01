package ru.naburnm8.queueserver.queueRule.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import java.util.UUID

@Entity
@Table(
    name = "queue_rules",
    indexes = [
        Index(name = "ix_queue_rules_plan", columnList = "queue_plan_id")
    ]
)
class QueueRule (
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_plan_id", nullable = false)
    var queuePlan: QueuePlan,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    var type: RuleType,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    var payload: String
)