package ru.naburnm8.queueserver.discipline.transporter

import ru.naburnm8.queueserver.discipline.request.AddWorkTypesRequest
import ru.naburnm8.queueserver.discipline.request.CreateNewDisciplineRequest
import java.util.UUID


object TransporterMapper {
    fun map(req: AddWorkTypesRequest, identity: UUID): AddWorkTypesTransporter {
        return AddWorkTypesTransporter(
            identity = identity,
            disciplineId = req.disciplineId,
            workTypes = req.workTypes.map {workTypeDto -> WorkTypeTransporter(name = workTypeDto.name, estimatedTimeMinutes = workTypeDto.estimatedTimeMinutes)},
        )
    }

    fun map(req: CreateNewDisciplineRequest, identity: UUID): CreateNewDisciplineTransporter {
        return CreateNewDisciplineTransporter(
            identity = identity,
            name = req.name
        )
    }
}