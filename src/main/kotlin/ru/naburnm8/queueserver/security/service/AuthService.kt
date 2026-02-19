package ru.naburnm8.queueserver.security.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.JwtProps
import ru.naburnm8.queueserver.security.TokenUtil
import ru.naburnm8.queueserver.security.entity.RefreshToken
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RefreshTokenRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import ru.naburnm8.queueserver.security.request.LoginRequest
import java.time.Instant


data class Tokens(val accessToken: String, val refreshToken: String)

@Service
class AuthService (
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val props: JwtProps,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    fun login(loginRequest: LoginRequest, userAgent: String?, ip: String?): Tokens {
        val user = userService.findByEmailOrNull(loginRequest.email) ?: throw RuntimeException(InnerExceptionCode.WRONG_EMAIL.toString())
        if (!user.isEnabled) throw RuntimeException(InnerExceptionCode.USER_DISABLED.toString())
        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) throw RuntimeException(InnerExceptionCode.WRONG_PASSWORD.toString())

        val access = issueAccessToken(user)
        val refreshValue = TokenUtil.newRefreshTokenValue()
        val refreshHash = TokenUtil.sha256Hex(refreshValue)

        val now = Instant.now()
        val refresh = RefreshToken(
            user = user,
            tokenHash = refreshHash,
            expiresAt = now.plusSeconds(props.refreshTtlSeconds),
            userAgent = userAgent,
            ip = ip,
        )
        refreshTokenRepository.save(refresh)

        return Tokens(accessToken = access, refreshToken = refreshValue)
    }

    @Transactional
    fun refresh(refreshTokenValue: String, userAgent: String?, ip: String?): Tokens {
        val now = Instant.now()
        val hash = TokenUtil.sha256Hex(refreshTokenValue)

        val token = refreshTokenRepository.findByTokenHash(hash) ?: throw RuntimeException("Invalid refresh token")
        if (!token.isActive(now)) throw RuntimeException("Refresh token expired or revoked")

        token.revokedAt = now

        val newValue = TokenUtil.newRefreshTokenValue()
        val newHash = TokenUtil.sha256Hex(newValue)
        token.replacedByHash = newHash

        val newEntity = RefreshToken(
            user = token.user,
            tokenHash = newHash,
            expiresAt = now.plusSeconds(props.refreshTtlSeconds),
            userAgent = userAgent,
            ip = ip
        )
        refreshTokenRepository.save(newEntity)

        val newAccess = issueAccessToken(token.user)
        return Tokens(accessToken = newAccess, refreshToken = newValue)
    }

    @Transactional
    fun logout(refreshTokenValue: String?) {
        if (refreshTokenValue.isNullOrBlank()) return
        val hash = TokenUtil.sha256Hex(refreshTokenValue)
        val token = refreshTokenRepository.findByTokenHash(hash) ?: return
        token.revokedAt = Instant.now()
    }

    @Transactional
    fun logoutAll(userEmail: String) {
        val user = userRepository.findByEmail(userEmail.trim().lowercase()) ?: return
        refreshTokenRepository.deleteAllByUserId(user.id)
    }

    private fun issueAccessToken(user: User): String {
        val now = Instant.now()
        val exp = now.plusSeconds(props.ttlSeconds)
        val roles = user.roles.map { it.name.name }

        val claims = JwtClaimsSet.builder()
            .issuer("adaptive-queue")
            .issuedAt(now)
            .expiresAt(exp)
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("roles", roles)
            .build()

        val headers = JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }
}