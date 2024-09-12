package com.inventory.model.dto;

import com.inventory.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private Long id;
    private String company;
    private LocalDateTime orderDate;
    private Boolean isActive;
    private List<OrderItemDTO> items;

    // Constructor to map from Order entity
    public OrderDTO(Long id, String company, LocalDateTime orderDate, Boolean isActive, List<OrderItemDTO> items) {
        this.id = id;
        this.company = company;
        this.orderDate = orderDate;
        this.isActive = isActive;
        this.items = items;
    }

    // Factory method to convert an Order entity to OrderDTO
    public static OrderDTO fromEntity(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(OrderItemDTO::fromEntity)
                .collect(Collectors.toList());
        return new OrderDTO(order.getId(), order.getCompany(), order.getOrderDate(), order.getActive(), itemDTOs);
    }

    // Getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
