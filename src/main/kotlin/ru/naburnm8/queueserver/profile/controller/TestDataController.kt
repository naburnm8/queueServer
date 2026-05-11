package ru.naburnm8.queueserver.profile.controller

import org.springframework.context.annotation.Profile
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.naburnm8.queueserver.profile.response.TestStudentResponse
import ru.naburnm8.queueserver.profile.service.TestDataService

@Profile("dev")
@RestController
@RequestMapping("/api/admin/testData")
@PreAuthorize("hasRole('ROLE_ADMIN')")
class TestDataController(
    private val testDataService: TestDataService
) {
    @PostMapping("/students")
    fun createStudents(
        @RequestParam count: Int
    ): List<TestStudentResponse> {
        return testDataService.createStudents(count)
    }

    @PostMapping("/students/csv", produces = ["text/csv"])
    fun createStudentsCsv(
        @RequestParam count: Int
    ): String {
        val students = testDataService.createStudents(count)

        return buildString {
            appendLine("email,password")
            students.forEach {
                appendLine("${it.email},${it.password}")
            }
        }
    }

    @DeleteMapping("/students")
    fun deleteLoadStudents() {
        testDataService.deleteLoadStudents()
    }
}