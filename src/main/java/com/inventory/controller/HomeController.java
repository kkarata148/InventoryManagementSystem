package com.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home"; // Ensure you have a corresponding home.html in templates
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin"; // Ensure you have a corresponding admin.html in templates
    }
}
