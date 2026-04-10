package ru.naburnm8.queueserver.security.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.repository.UserRepository

@Service
class AdminUserDetailsService (
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmailWithRoles(username.trim().lowercase()) ?: throw UsernameNotFoundException("${InnerExceptionCode.USER_NOT_FOUND}")
        val authorities : Collection<GrantedAuthority> = user.roles.map { SimpleGrantedAuthority(it.name.name) }

        return User(
            user.email,
            user.passwordHash,
            user.isEnabled,
            true,
            true,
            true,
            authorities
        )
    }

}