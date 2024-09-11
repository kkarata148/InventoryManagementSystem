package com.inventory.repository;

import com.inventory.model.ProductRack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRackRepository extends JpaRepository<ProductRack, Long> {
}
