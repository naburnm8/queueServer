package ru.naburnm8.queueserver.adminPanel.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateStudentForm
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateTeacherForm
import ru.naburnm8.queueserver.adminPanel.service.AdminService
import java.util.UUID

@Controller
@RequestMapping("/admin/users")
class AdminUsersController (
    private val adminService: AdminService,
) {
    @GetMapping("/students/create")
    fun createStudentPage(model: Model): String {
        model.addAttribute("form", AdminCreateStudentForm())
        return "admin/create-student"
    }

    @PostMapping("/students/create")
    fun createStudent(@ModelAttribute form: AdminCreateStudentForm): String {
        adminService.createStudent(form)
        return "redirect:/admin"
    }

    @GetMapping("/teachers/create")
    fun createTeacherPage(model: Model): String {
        model.addAttribute("form", AdminCreateTeacherForm())
        return "admin/create-teacher"
    }

    @PostMapping("/teachers/create")
    fun createTeacher(@ModelAttribute form: AdminCreateTeacherForm): String {
        adminService.createTeacher(form)
        return "redirect:/admin"
    }

    @PostMapping("/{id}/delete")
    fun deleteUser(@PathVariable id: UUID): String {
        adminService.deleteUser(id)
        return "redirect:/admin"
    }
}