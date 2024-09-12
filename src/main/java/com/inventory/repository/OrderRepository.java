package com.inventory.repository;

import com.inventory.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findOrderWithItems(@Param("orderId") Long orderId);
    List<Order> findByOrderDateNotNull();

    Order findByIsActive(boolean b);
}
