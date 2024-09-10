package com.inventory.service.impl;

import com.inventory.model.Order;
import com.inventory.model.Product;
import com.inventory.model.SoldProduct;
import com.inventory.repository.SoldProductRepository;
import com.inventory.service.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SoldProductServiceImpl implements SoldProductService {

    private final SoldProductRepository soldProductRepository;

    @Autowired
    public SoldProductServiceImpl(SoldProductRepository soldProductRepository) {
        this.soldProductRepository = soldProductRepository;
    }

    @Override
    public void sellProduct(Product product, Order order) {
        SoldProduct soldProduct = new SoldProduct();
        soldProduct.setSku(product.getSku());
        soldProduct.setName(product.getName());
        soldProduct.setSoldDate(LocalDateTime.now());
        soldProduct.setQuantity(product.getQuantity());
        soldProduct.setCompany(order.getCompany());

        soldProductRepository.save(soldProduct);
    }


    @Override
    public List<SoldProduct> getAllSoldProducts() {
        return soldProductRepository.findAll();
    }
}
