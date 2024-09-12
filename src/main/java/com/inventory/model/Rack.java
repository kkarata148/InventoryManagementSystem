package com.inventory.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "racks")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int totalCapacity;

    @Column(nullable = false)
    private int usedCapacity = 0;

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductRack> productRacks = new ArrayList<>();

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RackGap> gaps = new ArrayList<>();

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

    public List<RackGap> getGaps() {
        return gaps;
    }

    public void setGaps(List<RackGap> gaps) {
        this.gaps = gaps;
    }

    // Check if the rack has available space
    public boolean hasSpace() {
        return usedCapacity < totalCapacity;
    }

    // New method to maintain backward compatibility
    public int getCapacity() {
        return totalCapacity - usedCapacity;
    }

    // Add a gap to the rack
    public void addGap(int start, int end) {
        RackGap newGap = new RackGap();
        newGap.setStartPosition(start);
        newGap.setEndPosition(end);
        newGap.setRack(this);
        this.gaps.add(newGap);
        mergeAdjacentGaps();
    }

    // Clear all gaps from the rack
    public void clearGaps() {
        gaps.clear();
    }

    // Merge adjacent gaps to maintain consistency
    public void mergeAdjacentGaps() {
        gaps.sort(Comparator.comparingInt(RackGap::getStartPosition));
        List<RackGap> mergedGaps = new ArrayList<>();

        for (RackGap gap : gaps) {
            if (mergedGaps.isEmpty() || mergedGaps.get(mergedGaps.size() - 1).getEndPosition() < gap.getStartPosition() - 1) {
                mergedGaps.add(gap);
            } else {
                mergedGaps.get(mergedGaps.size() - 1).setEndPosition(Math.max(mergedGaps.get(mergedGaps.size() - 1).getEndPosition(), gap.getEndPosition()));
            }
        }

        gaps = mergedGaps; // Replace the old gaps with merged gaps
    }

    // Get a gap that has enough space for the given quantity
    public RackGap getGapWithEnoughSpace(int quantity) {
        for (RackGap gap : gaps) {
            if ((gap.getEndPosition() - gap.getStartPosition() + 1) >= quantity) {
                return gap; // Return the gap if enough space
            }
        }
        return null; // No gap with enough space
    }

    // Remove a gap from the rack
    public void removeGap(RackGap gap) {
        gaps.remove(gap);
    }

    // Check if the rack is empty
    public boolean isEmpty() {
        return this.usedCapacity == 0;
    }
}
