package ru.naburnm8.queueserver.adminPanel.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.naburnm8.queueserver.adminPanel.form.AdminCreateIntegrationForm
import ru.naburnm8.queueserver.adminPanel.service.AdminService
import java.util.UUID

@Controller
@RequestMapping("/admin/integrations")
class AdminIntegrationsController (
    private val adminService: AdminService,
) {
    @GetMapping("/create")
    fun createPage(model: Model): String {
        model.addAttribute("form", AdminCreateIntegrationForm())
        return "admin/create-integration"
    }

    @PostMapping("/create")
    fun create(@ModelAttribute form: AdminCreateIntegrationForm): String {
        adminService.createIntegration(form)
        return "redirect:/admin"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: UUID): String {
        adminService.deleteIntegration(id)
        return "redirect:/admin"
    }
}