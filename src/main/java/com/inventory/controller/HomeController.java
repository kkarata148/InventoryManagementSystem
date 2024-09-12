package com.inventory.controller;

import com.inventory.service.RackService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RackService rackService;

    public HomeController(RackService rackService) {
        this.rackService = rackService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getName().equals("anonymousUser")) {
            return "home";
        }

        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
