package ru.naburnm8.queueserver.exception

enum class InnerExceptionCode {
    WRONG_EMAIL,
    WRONG_PASSWORD,
    USER_DISABLED,
    USER_ALREADY_EXISTS,
    HASH_NOT_CALCULATED,
    USER_NOT_FOUND,
    NO_SUCH_DISCIPLINE,
    DISCIPLINE_NOT_OWNED,
    NO_SUCH_WORK_TYPE,
    SCHEMA_CORRUPTION,
}