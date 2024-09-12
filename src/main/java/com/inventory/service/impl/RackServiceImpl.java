package com.inventory.service.impl;

import com.inventory.model.Rack;
import com.inventory.repository.RackRepository;
import com.inventory.service.RackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.List;

@Service
public class RackServiceImpl implements RackService {

    @Autowired
    private RackRepository rackRepository;

    @Override
    @Transactional
    public List<Rack> findAllRacks() {
        List<Rack> racks = rackRepository.findAll();
        for (Rack rack : racks) {
            Hibernate.initialize(rack.getProductRacks());
        }
        return racks;
    }
    @Override
    @Transactional
    public Rack findRackById(Long id) {
        return rackRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Rack saveRack(Rack rack) {
        return rackRepository.save(rack);
    }

    @Override
    public void deleteRack(Long id) {
        rackRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Rack findFirstAvailableRack() {
        List<Rack> racks = rackRepository.findAll();
        for (Rack rack : racks) {
            if (rack.hasSpace()) {
                return rack;
            }
        }
        return null; // If no rack is available
    }
    @Override
    @Transactional
    public int getTotalOccupiedPlaces() {
        List<Rack> racks = rackRepository.findAll();
        int totalOccupiedPlaces = 0;
        for (Rack rack : racks) {
            totalOccupiedPlaces += rack.getUsedCapacity(); // Assuming Rack has a method getUsedCapacity
        }
        return totalOccupiedPlaces;
    }

    @Override
    @Transactional
    public int getAvailableSpaces() {
        List<Rack> racks = rackRepository.findAll();
        int availableSpaces = 0;
        for (Rack rack : racks) {
            availableSpaces += rack.getCapacity() - rack.getUsedCapacity(); // Assuming Rack has getCapacity
        }
        return availableSpaces;
    }
}
