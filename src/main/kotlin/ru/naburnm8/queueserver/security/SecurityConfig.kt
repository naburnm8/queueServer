package ru.naburnm8.queueserver.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
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
    private val props: JwtProps,
    private val adminUserDetailsService: UserDetailsService
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
    fun adminAuthenticationProvider(passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider(adminUserDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    @Order(1)
    fun adminPanelSecurityFilterChain(http: HttpSecurity, adminAuthenticationProvider: DaoAuthenticationProvider): SecurityFilterChain {
        http.securityMatcher("/admin/**")
            .authenticationProvider(adminAuthenticationProvider)
            .authorizeHttpRequests{
                it.requestMatchers("/admin/login", "/admin/login/**").permitAll()
                it.anyRequest().hasRole("ADMIN")
            }.formLogin {
                it.loginPage("/admin/login")
                it.loginProcessingUrl("/admin/login")
                it.defaultSuccessUrl("/admin", true)
                it.failureUrl("/admin/login?error")
            }.logout {
                it.logoutUrl("/admin/logout")
                it.logoutSuccessUrl("/admin/login?logout")
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }.csrf (Customizer.withDefaults())

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf{it.disable()}
            .sessionManagement{it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/actuator/health").permitAll()
                it.requestMatchers("/api/dev/**").permitAll()
                it.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                it.requestMatchers("/ws", "/ws/**").permitAll()
                it.requestMatchers("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.svg").permitAll()
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