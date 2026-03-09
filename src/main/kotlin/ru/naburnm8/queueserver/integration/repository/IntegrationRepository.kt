package ru.naburnm8.queueserver.integration.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.naburnm8.queueserver.integration.entity.Integration
import java.util.UUID

interface IntegrationRepository : JpaRepository<Integration, UUID> {
}