package com.inventory.service.impl;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.model.UserRole;
import com.inventory.model.dto.UserLoginDto;
import com.inventory.model.dto.UserRegisterDto;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import com.inventory.service.CurrentUser;
import com.inventory.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }

    @Override
    public boolean register(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            return false;
        }
        Optional<User> optionalUser = userRepository.findByEmailOrUsername(userRegisterDto.getEmail(), userRegisterDto.getUsername());
        if (optionalUser.isPresent()) {
            return false;
        }
        User user = modelMapper.map(userRegisterDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(UserRole.ROLE_EMPLOYEE);
        user.getRoles().add(userRole);
        userRepository.save(user);

        return true;
    }

    @Override
    public boolean login(UserLoginDto userLoginDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userLoginDto.getUsername());
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();

        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            return false;
        }

        currentUser.login(user);

        return true;
    }

    @Override
    public void logout() {
        currentUser.logout();
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleRepository.findByName(UserRole.valueOf(roleName));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public Set<Role> findAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Override
    public void updateUserRoles(Long userId, Set<String> roles) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<Role> roleSet = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleRepository.findByName(UserRole.valueOf(roleName));
            roleSet.add(role);
        }
        user.setRoles(roleSet);
        userRepository.save(user);
    }

    @Override
    public void initFirstUserAsAdmin() {
        if (userRepository.count() > 0) {
            return;
        }

        User firstUser = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(UserRole.ROLE_ADMIN));

        firstUser.setUsername("admin");
        firstUser.setPassword(passwordEncoder.encode("admin"));
        firstUser.setEmail("admin@example.com");
        firstUser.setRoles(roles);

        userRepository.save(firstUser);
    }
}
