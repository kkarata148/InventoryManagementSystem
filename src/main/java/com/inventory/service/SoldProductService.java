package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.SoldProduct;
import com.inventory.model.Order;
import java.util.List;

public interface SoldProductService {
    void sellProduct(Product product, Order order); // Updated method signature
    List<SoldProduct> getAllSoldProducts();
}
