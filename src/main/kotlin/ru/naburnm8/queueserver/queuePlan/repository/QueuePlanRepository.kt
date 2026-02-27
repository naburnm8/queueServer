package ru.naburnm8.queueserver.queuePlan.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import java.util.UUID

interface QueuePlanRepository: JpaRepository<QueuePlan, UUID> {
    fun findAllByDisciplineId(disciplineId: UUID): List<QueuePlan>
    fun findAllByCreatedByUserId(teacherId: UUID): List<QueuePlan>


    @Query("""
        select case when count(q) > 0 then true else false end
        from QueuePlan q
        where q.id = :queuePlanId and q.createdBy.userId = :teacherId
    """)
    fun isCreator(queuePlanId: UUID, teacherId: UUID): Boolean

    @Query("""
        select q from QueuePlan q
        join fetch q.discipline d 
        where d.id = :id
    """)
    fun findWithDiscipline(id: UUID): QueuePlan?
}