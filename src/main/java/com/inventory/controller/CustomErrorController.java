package com.inventory.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // Add logic to handle errors here
        return "error"; // This should match the name of your error HTML template (error.html)
    }

    public String getErrorPath() {
        return "/error";
    }
}
