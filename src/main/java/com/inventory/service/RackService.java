package com.inventory.service;

import com.inventory.model.Rack;
import com.inventory.model.Product;

import java.util.List;

public interface RackService {

    List<Rack> findAllRacks();

    Rack findRackById(Long id);

    Rack saveRack(Rack rack);

    void deleteRack(Long id);

    Rack findFirstAvailableRack();

    int getTotalOccupiedPlaces();

    int getAvailableSpaces();
}
