package ru.naburnm8.queueserver.exception

object ExceptionCodeMapper {
    fun map(exception: Exception): String {
        return exception.message ?: "Unknown error"
    }
}