package ru.naburnm8.queueserver.healthCheck

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthCheckController {
    @GetMapping()
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity("OK", HttpStatus.OK)
    }

    @GetMapping("/consumer")
    @PreAuthorize("hasRole('ROLE_QCONSUMER')")
    fun healthCheckConsumer(): ResponseEntity<String> {
        return ResponseEntity("OK", HttpStatus.OK)
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('ROLE_QOPERATOR')")
    fun healthCheckOperator(): ResponseEntity<String> {
        return ResponseEntity("OK", HttpStatus.OK)
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun healthCheckAdmin(): ResponseEntity<String> {
        return ResponseEntity("OK", HttpStatus.OK)
    }
}