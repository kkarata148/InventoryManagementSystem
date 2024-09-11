package com.inventory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();// Ensure you have a corresponding home.html in templates
        if (!authentication.getName().equals("anonymousUser")) {
            return "home"; // Redirect to the home page if the user is authenticated
        }

        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin"; // Ensure you have a corresponding admin.html in templates
    }
}
