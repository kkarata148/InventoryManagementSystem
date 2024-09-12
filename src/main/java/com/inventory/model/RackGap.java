package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rack_gaps")
public class RackGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int startPosition;

    private int endPosition;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    public RackGap() {
    }

    public RackGap(int startPosition, int endPosition, Rack rack) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.rack = rack;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
}
