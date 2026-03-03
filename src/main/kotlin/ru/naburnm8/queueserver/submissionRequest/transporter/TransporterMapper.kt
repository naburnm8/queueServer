package ru.naburnm8.queueserver.submissionRequest.transporter

import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import ru.naburnm8.queueserver.submissionRequest.request.SubmissionRequestRequest
import ru.naburnm8.queueserver.submissionRequest.response.SubmissionRequestItemResponse
import ru.naburnm8.queueserver.submissionRequest.response.SubmissionRequestResponse
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
                    workTypeName = it.workType.name,
                    minutesPerOne = it.workType.estimatedTimeMinutes
                )
            }
        )
    }

    fun toTransporter(req: SubmissionRequestRequest): InSubmissionRequestTransporter {
        return InSubmissionRequestTransporter(
            id = req.id,
            inviteCode = req.inviteCode,
            items = req.items.map {
                RequestItemTransporter(
                    workTypeId = it.workTypeId,
                    quantity = it.quantity,
                    minutesOverride = it.minutesOverride,
                    workTypeName = "",
                    minutesPerOne = 0
                )
            }
        )
    }

    private fun mapItem(transporter: RequestItemTransporter): SubmissionRequestItemResponse {
        return SubmissionRequestItemResponse(
            workTypeId = transporter.workTypeId,
            quantity = transporter.quantity,
            minutesOverride = transporter.minutesOverride,
            workTypeName = transporter.workTypeName,
            minutesPerOne = transporter.minutesPerOne,
        )
    }

    fun map(transporter: OutSubmissionRequestTransporter): SubmissionRequestResponse {
        return SubmissionRequestResponse(
            id = transporter.id,
            queuePlanId = transporter.queuePlanId,
            studentId = transporter.studentId,
            status = transporter.status,
            createdAt = transporter.createdAt,
            updatedAt = transporter.updatedAt,
            totalMinutes = transporter.totalMinutes,
            items = transporter.items.map {mapItem(it)}
        )
    }


}