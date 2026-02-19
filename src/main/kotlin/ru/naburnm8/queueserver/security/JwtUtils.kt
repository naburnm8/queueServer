package ru.naburnm8.queueserver.security


import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

object JwtUtils {
    fun currentAuthenticatedUserId(): UUID? {
        val auth = SecurityContextHolder.getContext().authentication
        val jwt = (auth ?: return null).principal as Jwt
        return UUID.fromString(jwt.subject)
    }
}