package ru.naburnm8.queueserver.integration.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnTransformer
import java.util.UUID

@Entity
@Table(name = "integrations")
class Integration (

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String,

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    var payload: String,
) {
}