package com.inventory.service.impl;

import com.inventory.model.Role;
import com.inventory.model.UserRole;
import com.inventory.repository.RoleRepository;
import com.inventory.service.RoleService;
import org.springframework.stereotype.Service;
import com.inventory.model.Product;

import java.util.Arrays;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void initRoles() {
        if (roleRepository.count() > 0) {
            return;
        }

        Arrays.stream(UserRole.values()).forEach(r -> {
            Role role = new Role();
            role.setName(r);
            roleRepository.saveAndFlush(role);
        });
    }
}
