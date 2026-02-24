package ru.naburnm8.queueserver.studentMetrics.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    val discipline: Discipline,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    val teacher: Teacher,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,

    @Column(name = "debts_count", nullable = false)
    var debtsCount: Int,
    @Column(name="personal_achievments_score", nullable = false)
    var personalAchievementsScore: Int,
) {
}