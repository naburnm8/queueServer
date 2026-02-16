package ru.naburnm8.queueserver.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
class JwtProps {
    lateinit var secret: String
    var ttlSeconds: Long = 3600
}