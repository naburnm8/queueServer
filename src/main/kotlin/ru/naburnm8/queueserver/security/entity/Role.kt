package ru.naburnm8.queueserver.security.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import ru.naburnm8.queueserver.security.RoleName
import java.util.UUID

@Entity
@Table(name = "roles")
class Role (
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 255)
    val name: RoleName = RoleName.ROLE_QCONSUMER
)