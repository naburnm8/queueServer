package ru.naburnm8.queueserver

import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.Role
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RoleRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import tools.jackson.databind.ObjectMapper

// configure environment variables for test db connection

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun createMockUser() {

        val foundUser = userRepository.findByEmail("testUser@test.test")

        if (foundUser != null) {
            return
        }

        val role = roleRepository.findByName(RoleName.ROLE_QCONSUMER) ?: roleRepository.save(
            Role(
                name = RoleName.ROLE_QCONSUMER,
            )
        )

        val user = User(
            email = "testUser@test.test",
            passwordHash = passwordEncoder.encode("1234567") ?: "",
            isEnabled = true,
            roles = mutableSetOf(role)
        )

        userRepository.save(user)
    }

    @Transactional
    @Test
    fun `login returns access and refresh tokens`() {
        val requestBody = mapOf(
            "email" to "testUser@test.test",
            "password" to "1234567"
        )

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestBody)
        }.andExpect {
            status { is2xxSuccessful() }
            jsonPath("$.accessToken") {exists()}
            jsonPath("$.refreshToken") {exists()}
        }
    }

    @AfterEach
    fun deleteMockUser() {
        userRepository.deleteByEmail("testUser@test.test")
    }

}