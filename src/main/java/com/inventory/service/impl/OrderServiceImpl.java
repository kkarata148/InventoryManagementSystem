package com.inventory.service.impl;

import com.inventory.model.*;
import com.inventory.repository.*;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductRackRepository productRackRepository;
    private final RackRepository rackRepository;
    private final RackGapRepository rackGapRepository;
    private Order currentOrder;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, ProductRackRepository productRackRepository, RackRepository rackRepository, RackGapRepository rackGapRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productRackRepository = productRackRepository;
        this.rackRepository = rackRepository;
        this.rackGapRepository = rackGapRepository;
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
        Iterator<OrderItem> iterator = order.getItems().iterator();

        while (iterator.hasNext()) {
            OrderItem item = iterator.next();
            Product product = item.getProduct();

            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            if (!product.getProductRacks().isEmpty()) {
                handleProductRackPosition(product, item.getQuantity());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());

            if (product.getQuantity() == 0) {
                productRepository.save(product);
            } else {
                productRepository.save(product);
            }
        }

        order.setOrderDate(LocalDateTime.now());
        order.setActive(false);
        orderRepository.save(order);
    }

    private void handleProductRackPosition(Product product, int quantityToRemove) {
        int remainingQuantity = quantityToRemove;

        List<ProductRack> productRacks = product.getProductRacks();

        for (int i = productRacks.size() - 1; i >= 0; i--) {
            ProductRack productRack = productRacks.get(i);

            if (remainingQuantity == 0) break;

            Rack rack = productRack.getRack();
            int productRackQuantity = productRack.getLastPosition() - productRack.getFirstPosition() + 1;

            if (remainingQuantity >= productRackQuantity) {
                remainingQuantity -= productRackQuantity;
                rack.setUsedCapacity(rack.getUsedCapacity() - productRackQuantity);

                // Remove the ProductRack and adjust the Rack's gaps
                product.getProductRacks().remove(productRack);
                rack.getProductRacks().remove(productRack);

                this.productRackRepository.delete(productRack);

                // Adjust gaps after full product removal
                if (rack.getUsedCapacity() == 0) {
                    rack.clearGaps();
                } else {
                    // Create a new RackGap for the freed positions
                    RackGap newGap = new RackGap();
                    newGap.setStartPosition(productRack.getFirstPosition());
                    newGap.setEndPosition(productRack.getLastPosition());
                    newGap.setRack(rack);
                    rack.getGaps().add(newGap);
                }
                this.rackRepository.save(rack);
            } else {
                // Partial removal of product rack
                int newEndPosition = productRack.getLastPosition() - remainingQuantity;

                // Find the highest LastPosition in the rack (to check if it's the last product)
                int highestLastPosition = rack.getProductRacks().stream()
                        .mapToInt(ProductRack::getLastPosition)
                        .max()
                        .orElse(0);

                // If this product rack is at the end, adjust the used capacity
                if (productRack.getLastPosition() == highestLastPosition && rack.getGaps().isEmpty()) {
                    rack.setUsedCapacity(newEndPosition);
                } else if (productRack.getLastPosition() == highestLastPosition) {
                    int totalGapSpace = 0;

                    for (RackGap gap : rack.getGaps()) {
                        if (gap.getEndPosition() < rack.getTotalCapacity()) {
                            totalGapSpace += (gap.getEndPosition() - gap.getStartPosition() + 1);
                        }
                    }
                    int newUsedCapacity = newEndPosition - totalGapSpace;                    // Ensure usedCapacity does not go below 0
                    rack.setUsedCapacity(Math.max(0, newUsedCapacity));
                } else {
                    // Add a gap for the removed portion
                    RackGap newGap = new RackGap();
                    newGap.setStartPosition(newEndPosition + 1);
                    newGap.setEndPosition(productRack.getLastPosition());
                    newGap.setRack(rack);
                    rack.getGaps().add(newGap);
                    this.rackGapRepository.save(newGap);

                    // Adjust the used capacity
                    rack.setUsedCapacity(rack.getUsedCapacity() - (productRack.getLastPosition() - newEndPosition));
                }

                // Update the ProductRack entry with the new end position
                productRack.setLastPosition(newEndPosition);
                productRackRepository.save(productRack);
                rackRepository.save(rack);
                remainingQuantity = 0;
            }
        }

        if (remainingQuantity > 0) {
            throw new IllegalArgumentException("Unable to remove the full quantity from racks.");
        }
    }

    @Override
    @Transactional
    public Order getCurrentOrder() {
        Order byIsActive = this.orderRepository.findByIsActive(true);
        return byIsActive;
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
        order.setActive(true);
        orderRepository.save(order);
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

