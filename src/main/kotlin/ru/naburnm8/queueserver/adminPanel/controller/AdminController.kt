package ru.naburnm8.queueserver.adminPanel.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.naburnm8.queueserver.adminPanel.service.AdminService

@Controller
@RequestMapping("/admin")
class AdminController (
    private val adminService: AdminService,
) {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("users", adminService.getAllUsers())
        model.addAttribute("integrations", adminService.getAllIntegrations())
        return "admin/index"
    }
}