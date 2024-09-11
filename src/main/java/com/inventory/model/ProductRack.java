package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_rack")
public class ProductRack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;

    @Column(name = "first_position", nullable = false)
    private Integer firstPosition;

    @Column(name = "last_position", nullable = false)
    private Integer lastPosition;

    public ProductRack() {}

    public ProductRack(Product product,Rack rack, Integer firstPosition, Integer lastPosition) {
        this.product = product;
        this.rack = rack;
        this.firstPosition = firstPosition;
        this.lastPosition = lastPosition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(Integer firstPosition) {
        this.firstPosition = firstPosition;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }
}
