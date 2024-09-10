package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status; // e.g., "Available", "Out of Stock"

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private double price;

    // Fields for Rack and Position in Rack
    @ManyToOne
    @JoinColumn(name = "rack_id", nullable = true)
    private Rack rack;

    @Column(nullable = true)
    private Integer rackPosition;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public Integer getRackPosition() {
        return rackPosition;
    }

    public void setRackPosition(Integer rackPosition) {
        this.rackPosition = rackPosition;
    }

    // Override methods like equals, hashCode, and toString as needed

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", category=" + category.getName() +  // Assuming Category has getName()
                ", price=" + price +
                ", rack=" + (rack != null ? rack.getName() : "No Rack") +
                ", rackPosition=" + (rackPosition != null ? rackPosition : "No Position") +
                '}';
    }
}
