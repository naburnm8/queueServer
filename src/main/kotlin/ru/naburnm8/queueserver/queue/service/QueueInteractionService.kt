package ru.naburnm8.queueserver.queue.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.queue.repository.QueueRuntimeStateRepository
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import ru.naburnm8.queueserver.submissionRequest.repository.SubmissionRequestRepository
import java.time.Instant
import java.util.UUID

@Service
class QueueInteractionService (
    private val runtimeRepository: QueueRuntimeStateRepository,
    private val queueRuntimeService: QueueRuntimeService,
    private val submissionRequestRepository: SubmissionRequestRepository,
    private val queuePlanRepository: QueuePlanRepository
) {
    @Transactional
    fun take(queuePlanId: UUID, requestId: UUID) {
        val runtime = runtimeRepository.findById(queuePlanId).orElseThrow()

        val snapshot = queueRuntimeService.getOrBuild(queuePlanId)

        val entry = snapshot.entries.find {it.requestId == requestId} ?: throw IllegalStateException("${InnerExceptionCode.NO_SUCH_SUBMISSION_REQUEST}")

        val request = submissionRequestRepository.findById(entry.requestId).orElseThrow()

        if (request.status != SubmissionStatus.ENQUEUED) throw IllegalStateException("${InnerExceptionCode.SCHEMA_CORRUPTION}")

        request.status = SubmissionStatus.DEQUEUED
        request.updatedAt = Instant.now()

        runtime.currentRequest = request
        runtime.takenAt = Instant.now()

        runtimeRepository.save(runtime)
        submissionRequestRepository.save(request)
        queueRuntimeService.refresh(queuePlanId)
    }


    @Transactional
    fun takeNext(queuePlanId: UUID) {
        val runtime = runtimeRepository.findById(queuePlanId).orElseThrow()

        val snapshot = queueRuntimeService.getOrBuild(queuePlanId)

        val next = snapshot.entries.firstOrNull() ?: throw IllegalStateException("${InnerExceptionCode.QUEUE_EMPTY}")

        val request = submissionRequestRepository.findById(next.requestId).orElseThrow()
        request.status = SubmissionStatus.DEQUEUED
        request.updatedAt = Instant.now()

        runtime.currentRequest = request
        runtime.takenAt = Instant.now()

        runtimeRepository.save(runtime)
        submissionRequestRepository.save(request)

        queueRuntimeService.refresh(queuePlanId)
    }

    @Transactional
    fun getStatus(queuePlanId: UUID): QueueStatus {
        val queuePlan = queuePlanRepository.findById(queuePlanId).orElseThrow()
        return queuePlan.status
    }

}