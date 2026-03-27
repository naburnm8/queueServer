package ru.naburnm8.queueserver.queue.service

import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.queue.data.QueueSnapshot
import ru.naburnm8.queueserver.queue.websocket.QueueWebsocketPublisher
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import java.time.Instant

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock


@Service
class QueueRuntimeService (
    private val queueViewService: QueueViewService,
    private val wsPublisher: QueueWebsocketPublisher,
    private val queuePlanRepository: QueuePlanRepository,
) {
    private val cache = ConcurrentHashMap<UUID, QueueSnapshot>()
    private val locks = ConcurrentHashMap<UUID, ReentrantLock>()
    private val versionCounter = AtomicLong(0)

    fun getOrBuild(queuePlanId: UUID): QueueSnapshot = cache[queuePlanId] ?: refresh(queuePlanId)

    fun refresh(queuePlanId: UUID): QueueSnapshot {
        val lock = locks.computeIfAbsent(queuePlanId) { ReentrantLock() }
        lock.lock()
        try {
            val version = versionCounter.getAndIncrement()
            val snapshot = queueViewService.buildSnapshot(queuePlanId, version)
            if (snapshot.generatedAt.toString() != Instant.EPOCH.toString()) {
                cache[queuePlanId] = snapshot
            }
            wsPublisher.publishChanged(queuePlanId, version)
            return snapshot
        } finally {
            lock.unlock()
        }
    }

    fun refreshByDiscipline(disciplineId: UUID): List<QueueSnapshot> {
        val queuePlans = queuePlanRepository.findAllByDisciplineId(disciplineId)
        return queuePlans.map { queuePlan -> refresh(queuePlan.id) }
    }

    fun invalidate(queuePlanId: UUID) {
        cache.remove(queuePlanId)
    }
}