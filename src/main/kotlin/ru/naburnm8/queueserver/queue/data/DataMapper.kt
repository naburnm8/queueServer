package ru.naburnm8.queueserver.queue.data

import ru.naburnm8.queueserver.queue.response.QueueEntryViewResponse
import ru.naburnm8.queueserver.queue.response.QueueSnapshotResponse

object DataMapper {
    fun map(data: QueueEntryView?) : QueueEntryViewResponse? {
        if (data == null) return null

        return QueueEntryViewResponse(
            studentId = data.studentId,
            studentName = data.studentName,
            place = data.place,
            studentAvatarUrl = data.studentAvatarUrl,
            totalMinutes = data.totalMinutes,
            priority = data.priority,
            status = data.status,
            requestId = data.requestId,
        )
    }


    fun map(data: QueueSnapshot): QueueSnapshotResponse {
        return QueueSnapshotResponse(
            queuePlanId = data.queuePlanId,
            version = data.version,
            generatedAt = data.generatedAt,
            current = map(data.current),
            entries = data.entries.map { entry ->
                QueueEntryViewResponse(
                    studentId = entry.studentId,
                    studentName = entry.studentName,
                    place = entry.place,
                    studentAvatarUrl = entry.studentAvatarUrl,
                    totalMinutes = entry.totalMinutes,
                    priority = entry.priority,
                    status = entry.status,
                    requestId = entry.requestId,
                )
            }
        )
    }
}