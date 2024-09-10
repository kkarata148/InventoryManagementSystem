package com.inventory.repository;

import com.inventory.model.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {
    // Additional query methods if needed
}
