
package com.inventory.repository;

import com.inventory.model.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    @Query("SELECT r FROM Rack r LEFT JOIN FETCH r.products")
    List<Rack> findAllWithProducts();
}

