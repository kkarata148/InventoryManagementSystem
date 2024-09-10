package com.inventory.service;

import java.time.LocalDateTime;
import com.inventory.model.Order;
import com.inventory.model.OrderItem;

import java.util.List;

public interface OrderService {
    Order addItemToOrder(Long orderId, Long productId, int quantity);
    void finalizeOrder(Long orderId);
    Order getOrderById(Long orderId);
    List<Order> getAllOrders();
    Order getCurrentOrder();  // New method to get the current active order
    void updateOrderItemQuantity(Long itemId, int newQuantity);
    void removeItemFromOrder(Long itemId);  // New method to remove an item from the order
    List<Order> getAllFinalizedOrders();
    Order createOrder(String company, LocalDateTime orderDate);
    int getTotalProductsSold(); // Method to calculate the total number of products sold
}
