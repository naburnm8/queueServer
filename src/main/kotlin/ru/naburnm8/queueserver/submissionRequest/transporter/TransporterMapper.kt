package ru.naburnm8.queueserver.submissionRequest.transporter

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import java.util.UUID

object TransporterMapper {

    fun toTransporter(entity: SubmissionRequest): OutSubmissionRequestTransporter {
        return OutSubmissionRequestTransporter(
            id = entity.id,
            queuePlanId = entity.queuePlan.id,
            studentId = entity.student.userId ?: UUID.fromString("00000000-0000-0000-0000-000000000000"),
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            totalMinutes = entity.totalMinutes(),
            items = entity.items.map {
                RequestItemTransporter(
                    workTypeId = it.workType.id,
                    quantity = it.quantity,
                    minutesOverride = it.minutesOverride,
                )
            }
        )
    }


}