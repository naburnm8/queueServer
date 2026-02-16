package ru.naburnm8.queueserver.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.security.entity.RefreshToken
import java.util.UUID

interface RefreshTokenRepository: JpaRepository<RefreshToken, UUID> {
    fun findByTokenHash(tokenHash: String): RefreshToken?
    fun deleteAllByUserId(userId: UUID): Long
}