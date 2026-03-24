package ru.naburnm8.queueserver.queue.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "queue_runtime_state")
class QueueRuntimeState (

    @Id
    @Column(name = "queue_plan_id")
    var queuePlanId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "queue_plan_id")
    var queuePlan: QueuePlan,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "current_request_id")
    var currentRequest: SubmissionRequest? = null,

    @Column(name = "taken_at")
    var takenAt: Instant? = null
) {
}