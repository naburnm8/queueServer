package ru.naburnm8.queueserver.queuePlan.entity

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
import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "queue_plans",
    indexes = [
        Index(name = "ix_queue_plans_discipline", columnList = "discipline_id"),
        Index(name = "ix_queue_plans_creator", columnList = "created_by")
    ]
)
class QueuePlan(

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id", nullable = false)
    var discipline: Discipline,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: Teacher,

    @Column(name = "title", nullable = false)
    var title: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: QueueStatus = QueueStatus.DRAFT,

    @Column(name = "use_debts", nullable = false)
    var useDebts: Boolean = true,

    @Column(name = "w_debts", nullable = false)
    var wDebts: Double = 1.0,

    @Column(name = "use_time", nullable = false)
    var useTime: Boolean = true,

    @Column(name = "w_time", nullable = false)
    var wTime: Double = 1.0,

    @Column(name = "use_achievements", nullable = false)
    var useAchievements: Boolean = true,

    @Column(name = "w_achievements", nullable = false)
    var wAchievements: Double = 1.0,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)