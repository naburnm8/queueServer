package ru.naburnm8.queueserver.submissionRequest.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import java.util.UUID

interface SubmissionRequestRepository: JpaRepository<SubmissionRequest, UUID> {

    fun findAllByQueuePlanId(queuePlanId: UUID): List<SubmissionRequest>

    fun findAllByQueuePlanIdAndStatus(queuePlanId: UUID, status: SubmissionStatus): List<SubmissionRequest>

    fun findByQueuePlanIdAndStudentUserId(queuePlanId: UUID, studentUserId: UUID): SubmissionRequest?

    fun existsByQueuePlanIdAndStudentUserId(queuePlanId: UUID, studentUserId: UUID): Boolean


    @Query(
        """
            select distinct r from SubmissionRequest r
            left join fetch r.items i
            left join fetch i.workType wt
            where r.queuePlan.id = :queuePlanId
        """
    )
    fun findAllWithItems(queuePlanId: UUID): List<SubmissionRequest>


    @Query("""
        select distinct r from SubmissionRequest r
        left join fetch r.items i
        left join fetch i.workType wt
        where r.queuePlan.id = :queuePlanId and r.status = ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus.ENQUEUED
        order by r.createdAt
    """)
    fun findEnqueuedWithItems(queuePlanId: UUID): List<SubmissionRequest>

    fun findAllByStudentUserId(studentUserId: UUID): List<SubmissionRequest>

}