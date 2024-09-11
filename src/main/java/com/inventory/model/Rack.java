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

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductRack> productRacks = new ArrayList<>();

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

    public List<ProductRack> getProductRacks() {
        return productRacks;
    }

    public void setProductRacks(List<ProductRack> productRacks) {
        this.productRacks = productRacks;
    }

    // Check if the rack has available space
    public boolean hasSpace() {
        return usedCapacity < totalCapacity;
    }

    // New method to maintain backward compatibility
    public int getCapacity() {
        return totalCapacity - usedCapacity;
    }
}
