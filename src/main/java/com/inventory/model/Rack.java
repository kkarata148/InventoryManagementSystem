package com.inventory.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "racks")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // For example, "Rack A", "Rack B"

    @Column(nullable = false)
    private int totalCapacity;

    @Column(nullable = false)
    private int usedCapacity = 0;

    @OneToMany(mappedBy = "rack", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>(); // Initialize the list

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(int usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Check if the rack has available space
    public boolean hasSpace() {
        return usedCapacity < totalCapacity;
    }

    // Assign a product to the rack
    public void assignProduct(Product product) {
        if (hasSpace()) {
            products.add(product);
            usedCapacity++;
        }
    }

    // New method to maintain backward compatibility
    public int getCapacity() {
        return totalCapacity - usedCapacity;
    }
}
