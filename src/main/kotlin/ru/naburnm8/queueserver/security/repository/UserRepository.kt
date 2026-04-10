package ru.naburnm8.queueserver.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.naburnm8.queueserver.security.entity.User
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun deleteByEmail(email: String)
    fun existsByEmail(email: String): Boolean

    @Query("""
        SELECT DISTINCT u
        from User u
        left join fetch u.roles
        where u.email = :email
    """)
    fun findByEmailWithRoles(email: String): User?

    @Query("""
        SELECT u
        from User u
        left join fetch u.roles
    """)
    fun findAllWithRoles(): List<User>
}