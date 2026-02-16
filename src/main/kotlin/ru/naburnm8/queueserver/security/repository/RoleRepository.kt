package ru.naburnm8.queueserver.security.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.Role
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: RoleName): Role?
}