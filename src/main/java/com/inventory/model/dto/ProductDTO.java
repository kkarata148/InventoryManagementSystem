package com.inventory.model.dto;

public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private int quantity;
    private double price;
    private Long categoryId;  // Use Long to represent the ID of the category
    private String status;    // Add the status field

    // Getters and Setters

    public Long getId() {  // Add this getter
        return id;
    }

    public void setId(Long id) {  // Add this setter
        this.id = id;
    }
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
