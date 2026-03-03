package ru.naburnm8.queueserver.invitation.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.invitation.repository.InvitationRepository
import java.time.Instant
import java.util.UUID

@Service
class InvitationMatcherService (
    private val invitationRepository: InvitationRepository
) {

    private fun checkWithCodeRestriction(queuePlanId: UUID, code: String, studentId: UUID? = null, targetGroup: String? = null): UUID? {
        if (studentId != null) {
            val found = invitationRepository.findCodeRestrictingActiveByStudent(queuePlanId, studentId, code, now = Instant.now())
            if (found.isNotEmpty()) return found[0].id
        } else if (targetGroup != null) {
            val found = invitationRepository.findCodeRestrictingActiveByGroup(queuePlanId, targetGroup, code, now = Instant.now())
            if (found.isNotEmpty()) return found[0].id
        }
        return null
    }

    private fun check(queuePlanId: UUID, code: String? = null, studentId: UUID? = null, targetGroup: String? = null): UUID? {
        if (code != null) {
            val found = invitationRepository.findActiveByCode(queuePlanId, code, now = Instant.now())
            if (found.isNotEmpty()) return found[0].id
        }
        if (studentId != null) {
            val found = invitationRepository.findActiveByStudent(queuePlanId, studentId, now = Instant.now())
            if (found.isNotEmpty()) return found[0].id
        }
        if (targetGroup != null) {
            val found = invitationRepository.findActiveByGroup(queuePlanId, targetGroup, now = Instant.now())
            if (found.isNotEmpty()) return found[0].id
        }
        return null
    }

    @Transactional
    fun matches(queuePlanId: UUID, studentId: UUID? = null, code: String? = null, targetGroup: String? = null): UUID? {
        var found: UUID? = null
        if (code != null && (targetGroup != null || studentId != null)) {
            found = checkWithCodeRestriction(queuePlanId, code, studentId, targetGroup)
        }
        if (found == null) {
            found = check(queuePlanId, code, studentId, targetGroup)
        }
        return found
    }

    @Transactional
    fun consume(invitationId: UUID) {
        val invitation = invitationRepository.findById(invitationId)
            .orElseThrow { RuntimeException("${InnerExceptionCode.NO_SUCH_INVITATION}") }

        if (invitation.isDepleted()) throw RuntimeException("${InnerExceptionCode.INVITATION_DEPLETED}")

        invitation.usedCount++
        invitationRepository.save(invitation)
    }

}