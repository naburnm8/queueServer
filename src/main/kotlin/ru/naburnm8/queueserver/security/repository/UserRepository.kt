package ru.naburnm8.queueserver.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.security.entity.User

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun deleteByEmail(email: String)
    fun existsByEmail(email: String): Boolean
}