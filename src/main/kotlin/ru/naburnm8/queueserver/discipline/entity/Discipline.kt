package ru.naburnm8.queueserver.discipline.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

@Entity
@Table(name = "disciplines")
class Discipline(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String = "",

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "disciplines_owners",
        joinColumns = [JoinColumn(name = "discipline_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "teacher_id", referencedColumnName = "user_id")]
    )
    var owners: MutableSet<Teacher> = mutableSetOf(),
)
