package ru.naburnm8.queueserver.submissionRequest.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.discipline.entity.WorkType
import java.util.UUID

@Entity
@Table(
    name = "submission_request_items",
    indexes = [
        Index(name = "ix_request_items_request", columnList = "request_id"),
        Index(name = "ix_request_items_work_type", columnList = "work_type_id")
    ],
)
class SubmissionRequestItem (

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "request_id", nullable = true)
    var request: SubmissionRequest?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_type_id", nullable = false)
    var workType: WorkType,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1,

    @Column(name = "minutes_override")
    var minutesOverride: Int? = null,

) {
    fun effectiveMinutes(): Int = (minutesOverride ?: workType.estimatedTimeMinutes) * quantity
}