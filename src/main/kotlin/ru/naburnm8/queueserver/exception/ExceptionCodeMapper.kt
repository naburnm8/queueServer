package ru.naburnm8.queueserver.exception

class ExceptionCodeMapper {
    companion object {
        fun map(exception: Exception): String {
            return exception.message ?: "Unknown error"
        }
    }
}