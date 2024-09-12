package com.inventory.model.dto;

import com.inventory.model.OrderItem;
import com.inventory.model.Product;

public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;

    // Constructor to map from OrderItem entity
    public OrderItemDTO(Long id, Long productId, String productName, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // Factory method to convert an OrderItem entity to OrderItemDTO
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        return new OrderItemDTO(orderItem.getId(), product.getId(), product.getName(), orderItem.getQuantity());
    }

    // Getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
