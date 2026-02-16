package ru.naburnm8.queueserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableMethodSecurity
class SecurityConfig (
    private val props: JwtProps
) {
    @Bean
    fun jwtAuthConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()

        val gac = JwtGrantedAuthoritiesConverter()
        gac.setAuthoritiesClaimName("roles")
        gac.setAuthorityPrefix("")

        converter.setJwtGrantedAuthoritiesConverter(gac)
        return converter
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val key = SecretKeySpec(props.secret.toByteArray(), "HmacSHA256")
        val jwk = org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256
        val jwkSource = org.springframework.security.oauth2.jwt.NimbusJwtEncoder(
            com.nimbusds.jose.jwk.source.ImmutableSecret(key.encoded)
        )
        return jwkSource
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key = SecretKeySpec(props.secret.toByteArray(), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(key).build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{it.disable()}
            .sessionManagement{it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/actuator/health").permitAll()
                it.requestMatchers("/api/dev/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {rs ->
                rs.jwt { jwt -> jwt.jwtAuthenticationConverter (jwtAuthConverter())}
            }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


}