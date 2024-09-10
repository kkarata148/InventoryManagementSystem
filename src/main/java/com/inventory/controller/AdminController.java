package com.inventory.controller;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/user-list";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{id}/roles")
    public String editUserRoles(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        Set<Role> roles = userService.findAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin/user-roles";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id, @RequestParam Set<String> roles) {
        userService.updateUserRoles(id, roles);
        return "redirect:/admin/users";
    }
}
