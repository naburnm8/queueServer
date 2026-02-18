package ru.naburnm8.queueserver.security.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID


@Entity
@Table(
    name = "refresh_tokens",
    indexes = [
        Index(name = "ix_refresh_tokens_user_id", columnList = "user_id"),
        Index(name = "ix_refresh_tokens_hash", columnList = "token_hash", unique = true)
    ]
)
class RefreshToken (
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    val tokenHash: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "revoked_at")
    var revokedAt: Instant? = null,

    @Column(name = "replaced_by_hash", length = 64)
    var replacedByHash: String? = null,

    @Column(name = "user_agent", length = 300)
    val userAgent: String? = null,

    @Column(name = "ip", length = 64)
    val ip: String? = null
) {
    fun isActive(now: Instant = Instant.now()): Boolean {
        return revokedAt == null || now.isBefore(expiresAt)
    }
}