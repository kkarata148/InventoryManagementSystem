package com.inventory.controller;

import com.inventory.model.Order;
import com.inventory.model.OrderItem;
import com.inventory.model.dto.OrderDTO;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;  // For LocalDateTime
import java.util.HashMap;
import java.util.Map;            // For Map


@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // This method returns the current order details as a webpage
    @GetMapping("/current")
    public String viewCurrentOrder(Model model) {
        try {
            Order currentOrder = orderService.getCurrentOrder();
            if (currentOrder == null) {
                return "order/no-active-order";  // Return a view that indicates no active order
            }

            // Log the order items to verify they're being fetched
            System.out.println("Order ID: " + currentOrder.getId());
            for (OrderItem item : currentOrder.getItems()) {
                System.out.println("Item: " + item.getProduct().getName() + ", Quantity: " + item.getQuantity());
            }

            model.addAttribute("order", currentOrder);
            return "order/current-order";
        } catch (IllegalStateException e) {
            return "order/no-active-order";  // Handle the case when there's no active order
        }
    }



    // This method is used to retrieve the current order data in JSON format (for AJAX requests)
    @GetMapping("/json")
    @ResponseBody
    public ResponseEntity<?> getCurrentOrderJson() {
        Order currentOrder = orderService.getCurrentOrder();
        if (currentOrder == null) {
            // Return proper JSON response for 404 (Not Found)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "No active order. Please create an order first.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        OrderDTO orderDTO = OrderDTO.fromEntity(currentOrder);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> requestBody) {
        String company = requestBody.get("company");
        String orderDateStr = requestBody.get("orderDate");

        // Remove the 'Z' and parse as LocalDateTime
        orderDateStr = orderDateStr.replace("Z", "");
        LocalDateTime orderDate = LocalDateTime.parse(orderDateStr);

        // Call the service method to create the order
        Order order = orderService.createOrder(company, orderDate);
        if (order == null) {
            return ResponseEntity.status(500).body("{\"error\":\"Failed to create order.\"}");
        }
        return ResponseEntity.ok(order);
    }



    @PostMapping("/{orderId}/add-to-order")
    @ResponseBody
    public ResponseEntity<?> addProductToOrder(@PathVariable Long orderId, @RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        Order order = orderService.getCurrentOrder();
        if (order == null) {
            return ResponseEntity.status(404).body("{\"error\":\"Order not found.\"}");
        }
        Order orderCreated = orderService.addItemToOrder(order.getId(), productId, quantity);
        return ResponseEntity.ok("{\"success\":\"Product added to order.\"}");
    }

    @PostMapping("/item/{itemId}/remove")
    @ResponseBody
    public ResponseEntity<?> removeItemFromOrder(@PathVariable Long itemId) {
        Order order = orderService.getCurrentOrder();
        if (order == null) {
            return ResponseEntity.status(404).body("{\"error\":\"Order not found.\"}");
        }
        orderService.removeItemFromOrder(itemId);
        return ResponseEntity.ok("{\"success\":\"Item removed from order.\"}");
    }

    @PostMapping("/item/{itemId}/update-quantity")
    @ResponseBody
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long itemId, @RequestParam("quantity") int quantity) {
        Order order = orderService.getCurrentOrder();
        if (order == null) {
            return ResponseEntity.status(404).body("{\"error\":\"Order not found.\"}");
        }
        orderService.updateOrderItemQuantity(itemId, quantity);
        return ResponseEntity.ok("{\"success\":\"Item quantity updated.\"}");
    }


    @PostMapping("/{orderId}/finalize")
    @ResponseBody
    public ResponseEntity<?> finalizeOrder(@PathVariable Long orderId) {
        try {
            orderService.finalizeOrder(orderId);
            return ResponseEntity.ok("{\"success\":\"Order finalized.\"}");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(500).body("{\"error\":\"Failed to finalize order.\"}");
        }
    }
}
