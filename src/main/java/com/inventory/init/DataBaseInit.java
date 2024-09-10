package com.inventory.init;

import com.inventory.service.RoleService;
import com.inventory.service.UserService;
import com.inventory.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataBaseInit implements CommandLineRunner {
    private final RoleService roleService;
    private final UserService userService;
    private final CategoryService categoryService;

    public DataBaseInit(RoleService roleService, UserService userService, CategoryService categoryService) {
        this.roleService = roleService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        roleService.initRoles();
        userService.initFirstUserAsAdmin();
        categoryService.initCategories();  // Initialize categories
    }
}
