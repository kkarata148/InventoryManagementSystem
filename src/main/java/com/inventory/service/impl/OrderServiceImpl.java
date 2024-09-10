package com.inventory.service.impl;

import com.inventory.model.Order;
import com.inventory.model.OrderItem;
import com.inventory.model.Product;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.OrderItemRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private Order currentOrder;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order addItemToOrder(Long orderId, Long productId, int quantity) {
        Order order = getCurrentOrder();
        if (order == null || !order.getId().equals(orderId)) {
            throw new IllegalStateException("No active order found or mismatched order ID.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        Optional<OrderItem> existingOrderItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingOrderItem.isPresent()) {
            OrderItem orderItem = existingOrderItem.get();
            orderItem.setQuantity(orderItem.getQuantity() + quantity);
            orderItemRepository.save(orderItem); // Save the updated order item
        } else {
            OrderItem orderItem = new OrderItem(order, product, quantity);
            order.getItems().add(orderItem);
            orderItemRepository.save(orderItem); // Save the new order item
        }

        orderRepository.save(order); // Save the updated order
        return order;
    }

    @Override
    @Transactional
    public void finalizeOrder(Long orderId) {
        Order order = getCurrentOrder();
        if (order == null || !order.getId().equals(orderId)) {
            throw new IllegalStateException("No active order found or mismatched order ID.");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
        currentOrder = null; // Clear the current order
    }

    @Override
    @Transactional
    public Order getCurrentOrder() {
        if (currentOrder == null || currentOrder.getId() == null) {
            log.info("No active order found. Returning null so the front-end can create one.");
            return null; // Allow the front-end to handle order creation
        }

        return orderRepository.findById(currentOrder.getId())
                .orElseThrow(() -> new IllegalStateException("Order not found for id: " + currentOrder.getId()));
    }



    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
    }
    @Override
    @Transactional
    public Order createOrder(String company, LocalDateTime orderDate) {
        Order order = new Order();
        order.setCompany(company);
        order.setOrderDate(orderDate);
        orderRepository.save(order);
        currentOrder = order; // Make sure currentOrder is set
        return order;
    }



    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public void updateOrderItemQuantity(Long itemId, int newQuantity) {
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order item ID"));
        orderItem.setQuantity(newQuantity);
        orderItemRepository.save(orderItem); // Save the updated quantity
    }

    @Override
    @Transactional
    public void removeItemFromOrder(Long itemId) {
        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order item ID"));
        Order order = orderItem.getOrder();
        order.getItems().remove(orderItem);
        orderItemRepository.delete(orderItem); // Delete the order item
        orderRepository.save(order); // Save the updated order
    }

    @Override
    public List<Order> getAllFinalizedOrders() {
        return orderRepository.findByOrderDateNotNull();
    }

    @Override
    @Transactional
    public int getTotalProductsSold() {
        List<Order> finalizedOrders = getAllFinalizedOrders();
        int totalSold = 0;
        for (Order order : finalizedOrders) {
            for (OrderItem item : order.getItems()) {
                totalSold += item.getQuantity();
            }
        }
        return totalSold;
    }
}

