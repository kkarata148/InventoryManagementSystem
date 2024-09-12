package com.inventory.repository;

import com.inventory.model.RackGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackGapRepository extends JpaRepository<RackGap, Long> {
}
