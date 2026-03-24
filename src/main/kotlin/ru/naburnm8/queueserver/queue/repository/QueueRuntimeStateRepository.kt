package ru.naburnm8.queueserver.queue.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.queue.entity.QueueRuntimeState
import java.util.UUID

interface QueueRuntimeStateRepository : JpaRepository<QueueRuntimeState, UUID> {

}