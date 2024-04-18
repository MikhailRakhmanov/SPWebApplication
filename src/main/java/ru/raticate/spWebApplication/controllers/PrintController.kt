package ru.raticate.spWebApplication.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import ru.raticate.spWebApplication.controllers.api.ScanController

@Controller
class PrintController(private val scanController: ScanController) {
    @GetMapping("/print")
    fun print(model: Model): String {
        model.addAttribute("platform", scanController.platform())
        return "print"
    }
}