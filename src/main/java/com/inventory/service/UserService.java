package com.inventory.service;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.model.dto.UserLoginDto;
import com.inventory.model.dto.UserRegisterDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    boolean register(UserRegisterDto userRegisterDto);

    boolean login(UserLoginDto userLoginDto);

    void logout();

    void addRoleToUser(String username, String roleName);

    List<User> findAllUsers();

    User findUserById(Long id);

    Set<Role> findAllRoles();

    void updateUserRoles(Long userId, Set<String> roles);

    void initFirstUserAsAdmin();  // Ensure this method is included
}
