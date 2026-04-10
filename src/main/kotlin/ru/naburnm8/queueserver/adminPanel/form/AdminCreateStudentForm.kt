package ru.naburnm8.queueserver.adminPanel.form

data class AdminCreateStudentForm(
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var patronymic: String? = null,
    var academicGroup: String = "",
    var telegram: String? = null,
    var avatarUrl: String? = null,
)
