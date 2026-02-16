package ru.naburnm8.queueserver.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.security.entity.User
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun deleteByEmail(email: String)
    fun existsByEmail(email: String): Boolean
}