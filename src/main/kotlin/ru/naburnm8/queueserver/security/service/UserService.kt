package ru.naburnm8.queueserver.security.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.naburnm8.queueserver.exception.InnerExceptionCode
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.Role
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RoleRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import ru.naburnm8.queueserver.security.request.RegisterRequest

@Service
class UserService (
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun findByEmailOrNull(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun registerQueueConsumer(request: RegisterRequest): User {
        val email = request.email
        val rawPassword = request.password

        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException(InnerExceptionCode.USER_ALREADY_EXISTS.toString())
        }

        val studentRole = getOrCreateRole(RoleName.ROLE_QCONSUMER)

        val user = User(
            email = email.trim().lowercase(),
            passwordHash = passwordEncoder.encode(rawPassword) ?: throw RuntimeException(InnerExceptionCode.HASH_NOT_CALCULATED.toString()),
            roles = mutableSetOf(studentRole)
        )

        return userRepository.save(user)
    }

    @Transactional
    fun createAdmin(request: RegisterRequest): User {
        val email = request.email
        val rawPassword = request.password

        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val adminRole = getOrCreateRole(RoleName.ROLE_ADMIN)

        val user = User(
            email = email.trim().lowercase(),
            passwordHash = passwordEncoder.encode(rawPassword) ?: throw RuntimeException(InnerExceptionCode.HASH_NOT_CALCULATED.toString()),
            roles = mutableSetOf(adminRole)
        )

        return userRepository.save(user)
    }


    private fun getOrCreateRole(roleName: RoleName): Role {
        return roleRepository.findByName(roleName) ?: roleRepository.save(Role(name = roleName))
    }

}