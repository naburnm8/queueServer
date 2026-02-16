package ru.naburnm8.queueserver.security.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.JwtProps
import ru.naburnm8.queueserver.security.request.LoginRequest
import java.time.Instant


@Service
class AuthService (
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val props: JwtProps
) {
    fun login(loginRequest: LoginRequest): String {
        val user = userService.findByEmailOrNull(loginRequest.email) ?: throw RuntimeException(InnerExceptionCode.WRONG_EMAIL.toString())
        if (!user.isEnabled) throw RuntimeException(InnerExceptionCode.USER_DISABLED.toString())
        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) throw RuntimeException(InnerExceptionCode.WRONG_PASSWORD.toString())

        val now = Instant.now()
        val expires = now.plusSeconds(props.ttlSeconds)

        val roles = user.roles.map {it.name.name}

        val claims = JwtClaimsSet.builder()
            .issuer("queue-server")
            .issuedAt(now)
            .expiresAt(expires)
            .subject(user.email)
            .claim("roles", roles)
            .build()

        val headers = JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build()

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }
}