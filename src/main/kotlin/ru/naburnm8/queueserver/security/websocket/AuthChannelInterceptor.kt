package ru.naburnm8.queueserver.security.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

@Configuration
class AuthChannelInterceptor (
    private val jwtDecoder: JwtDecoder,
    private val jwtAuthenticationConverter: JwtAuthenticationConverter
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        if (StompCommand.CONNECT == accessor.command) {
            val authHeader = accessor.getFirstNativeHeader("Authorization")
                ?: throw IllegalArgumentException("Missing Authorization header")

            if (!authHeader.startsWith("Bearer ")) {
                throw IllegalArgumentException("Invalid Authorization header")
            }

            val token = authHeader.removePrefix("Bearer ").trim()

            val jwt = jwtDecoder.decode(token)
            val authentication = jwtAuthenticationConverter.convert(jwt)
                ?: throw IllegalStateException("Cannot convert JWT to Authentication")

            accessor.user = authentication
        }

        return message
    }
}