package ru.naburnm8.queueserver.submissionRequest.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import java.time.Instant
import java.util.UUID


@Entity
@Table(
    name = "submission_requests",
    indexes = [
        Index(name = "ix_requests_queue", columnList = "queue_plan_id"),
        Index(name = "ix_requests_student", columnList = "student_id"),
        Index(name = "ix_requests_status", columnList = "status")
    ]
)
class SubmissionRequest (
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "queue_plan_id", nullable = false)
    var queuePlan: QueuePlan,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, referencedColumnName = "user_id")
    var student: Student,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SubmissionStatus = SubmissionStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    ) {

    @OneToMany(
        mappedBy = "request",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var items: MutableList<SubmissionRequestItem> = mutableListOf()

    fun totalMinutes(): Int = items.sumOf {it.effectiveMinutes()}

    fun addItem(item: SubmissionRequestItem) {
        if (!items.contains(item)) {
            items.add(item)
            item.request = this
            updatedAt = Instant.now()
        }
    }

    fun removeItem(item: SubmissionRequestItem) {
        if (items.remove(item)) {
            item.request = null
            updatedAt = Instant.now()
        }
    }

    fun clearItems() {
        items.forEach { it.request = null }
        items.clear()
        updatedAt = Instant.now()
    }
}