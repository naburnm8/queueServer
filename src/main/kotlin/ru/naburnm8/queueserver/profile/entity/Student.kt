package ru.naburnm8.queueserver.profile.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import ru.naburnm8.queueserver.security.entity.User
import java.util.UUID

@Entity
@Table(
    name = "students",
    indexes = [
        Index(name = "idx_student_group", columnList = "academic_group"),
        Index(name = "idx_student_telegram", columnList = "telegram"),
    ]
)
data class Student(

    @Id
    @Column(name = "user_id", nullable = false)
    var userId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "first_name", nullable = false)
    var firstName: String = "",

    @Column(name = "last_name", nullable = false)
    var lastName: String = "",

    @Column(name = "patronymic", nullable = false)
    var patronymic: String? = null,

    @Column(name = "academic_group", nullable = false)
    var academicGroup: String = "",

    @Column(name = "telegram")
    var telegram: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,
)
