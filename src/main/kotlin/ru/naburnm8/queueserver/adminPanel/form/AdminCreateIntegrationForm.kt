package ru.naburnm8.queueserver.adminPanel.form

data class AdminCreateIntegrationForm(
    var name: String = "",
    var baseUrl: String = "",
    var registerUrl: String = ""
)
