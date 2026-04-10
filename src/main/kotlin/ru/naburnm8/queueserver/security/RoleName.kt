package ru.naburnm8.queueserver.security

enum class RoleName {
    ROLE_QCONSUMER,
    ROLE_QOPERATOR,
    ROLE_ADMIN
}

fun RoleName.toReadableText(): String {
    when(this) {
        RoleName.ROLE_ADMIN -> return "Admin"
        RoleName.ROLE_QCONSUMER -> return "Consumer/Student"
        RoleName.ROLE_QOPERATOR -> return "Operator/Teacher"
    }
}