package ru.naburnm8.queueserver.security


import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import java.util.UUID

object JwtUtils {
    fun currentAuthenticatedUserId(): UUID? {
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = (auth ?: return null).principal as Jwt
        return UUID.fromString(jwt.subject)
    }

    fun getSubject(): UUID {
        val subject = currentAuthenticatedUserId() ?: throw RuntimeException(InnerExceptionCode.USER_NOT_FOUND.toString())
        return subject
    }
}