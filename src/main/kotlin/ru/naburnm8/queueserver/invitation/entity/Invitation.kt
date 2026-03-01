package ru.naburnm8.queueserver.invitation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import java.time.Instant
import java.util.UUID


@Entity
@Table(
    name = "invitations",
    indexes = [
        Index(name = "ix_invitations_queue_plan", columnList = "queue_plan_id"),
        Index(name = "ix_invitations_code", columnList = "code"),
        Index(name = "ix_invitations_group", columnList = "target_group"),
        Index(name = "ix_invitations_expires", columnList = "expires_at")
    ]
)
class Invitation (

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_plan_id", nullable = false)
    var queuePlan: QueuePlan,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: Teacher,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    var mode: InvitationMode,

    @Column(name = "code", length = 64)
    var code: String? = null,

    @Column(name = "target_group", length = 64)
    var targetGroup: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = Instant.MAX,

    @Column(name = "max_uses", nullable = false)
    var maxUses: Int = Int.MAX_VALUE,

    @Column(name = "used_count", nullable = false)
    var usedCount: Int = 0

    ) {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "invitation_students",
        joinColumns = [JoinColumn(name = "invitation_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id", referencedColumnName = "user_id")]
    )
    var targetStudents: MutableSet<Student> = mutableSetOf()

    fun isExpired(now: Instant = Instant.now()): Boolean {
        return now.isAfter(expiresAt)
    }

    fun isDepleted(): Boolean {
        return usedCount >= maxUses
    }

}