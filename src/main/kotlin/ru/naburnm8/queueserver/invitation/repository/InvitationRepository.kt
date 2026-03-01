package ru.naburnm8.queueserver.invitation.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.invitation.entity.Invitation
import java.time.Instant
import java.util.UUID

interface InvitationRepository: JpaRepository<Invitation, UUID> {

    @Query("""
        select i from Invitation i
        where i.mode = ru.naburnm8.queueserver.invitation.entity.InvitationMode.COMMON
        and i.queuePlan.id = :queuePlanId
        and i.enabled = true
        and i.code = :code
        and i.expiresAt > :now
    """)
    fun findActiveByCode(queuePlanId: UUID, code: String, now: Instant): List<Invitation>


    @Query("""
        select i from Invitation i
        where i.mode = ru.naburnm8.queueserver.invitation.entity.InvitationMode.COMMON
        and i.queuePlan.id = :queuePlanId
        and i.enabled = true
        and i.targetGroup = :targetGroup
        and i.expiresAt > :now
    """)
    fun findActiveByGroup(queuePlanId: UUID, targetGroup: String, now: Instant): List<Invitation>


    @Query("""
        select distinct i from Invitation i
        join i.targetStudents s
        where i.mode = ru.naburnm8.queueserver.invitation.entity.InvitationMode.COMMON
        and i.queuePlan.id = :queuePlanId
        and i.enabled = true
        and s.userId = :studentId
        and i.expiresAt > :now
    """)
    fun findActiveByStudent(queuePlanId: UUID, studentId: UUID, now: Instant): List<Invitation>

    @Query("""
        select i from Invitation i
        where i.mode = ru.naburnm8.queueserver.invitation.entity.InvitationMode.CODE_RESTRICTING
        and i.queuePlan.id = :queuePlanId
        and i.enabled = true
        and i.code = :code
        and i.targetGroup = :targetGroup
        and i.expiresAt > :now
    """)
    fun findCodeRestrictingActiveByGroup(queuePlanId: UUID, targetGroup: String, code: String, now: Instant): List<Invitation>

    @Query("""
        select distinct i from Invitation i
        join i.targetStudents s
        where i.mode = ru.naburnm8.queueserver.invitation.entity.InvitationMode.CODE_RESTRICTING
        and i.queuePlan.id = :queuePlanId
        and i.enabled = true
        and s.userId = :studentId
        and i.code = :code
        and i.expiresAt > :now
    """)
    fun findCodeRestrictingActiveByStudent(queuePlanId: UUID, studentId: UUID, code: String, now: Instant): List<Invitation>

    fun findAllByQueuePlanId(queuePlanId: UUID): List<Invitation>
}