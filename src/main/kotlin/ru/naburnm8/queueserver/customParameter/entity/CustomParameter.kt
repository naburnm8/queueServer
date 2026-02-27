package ru.naburnm8.queueserver.customParameter.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

// Задел на будущее
@Entity
@Table(
    name = "custom_parameters",
)
class CustomParameter (
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val creator: Teacher,

    val parameterBody: String
) {
}