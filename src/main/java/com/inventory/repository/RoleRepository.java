package com.inventory.repository;

import com.inventory.model.Role;
import com.inventory.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(UserRole userRole);
}
