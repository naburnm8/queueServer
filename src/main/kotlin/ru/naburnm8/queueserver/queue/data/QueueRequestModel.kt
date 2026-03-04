package ru.naburnm8.queueserver.queue.data

import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.studentMetrics.entity.StudentMetrics
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest

data class QueueRequestModel(
    val request: SubmissionRequest,
    val student: Student,
    val metrics: StudentMetrics?,
    val totalMinutes: Int
)
