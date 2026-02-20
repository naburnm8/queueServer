package ru.naburnm8.queueserver.studentMetrics.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import java.util.UUID

@Entity
@Table(name = "student_metrics")
class StudentMetrics(

    @Id
    @Column(name = "id", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val discipline: Discipline,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val teacher: Teacher,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val student: Student,

    @Column(name = "debts_count", nullable = false)
    val debtsCount: Int,
    @Column(name="personal_achievments_score", nullable = false)
    val personalAchievementsScore: Int,
) {
}