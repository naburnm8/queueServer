package ru.naburnm8.queueserver.adminPanel.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminLoginController {

    @GetMapping("/login")
    fun loginPage(): String = "admin/login"
}