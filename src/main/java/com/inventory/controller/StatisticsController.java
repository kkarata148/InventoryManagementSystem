package com.inventory.controller;

import com.inventory.model.Rack;
import com.inventory.service.ProductService;
import com.inventory.service.RackService;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {

    private final RackService rackService;
    private final OrderService orderService;

    @Autowired
    public StatisticsController(RackService rackService, OrderService orderService) {
        this.rackService = rackService;
        this.orderService = orderService;
    }

    @GetMapping("/statistics")
    public String viewStatistics(Model model) {
        // Fetch all racks
        List<Rack> racks = rackService.findAllRacks();
        model.addAttribute("racks", racks);

        // Fetch total products and total sold products
        int totalQuantity = racks.stream().mapToInt(Rack::getUsedCapacity).sum();
        int totalSold = orderService.getTotalProductsSold();

        model.addAttribute("totalProducts", totalQuantity);
        model.addAttribute("totalSold", totalSold);

        return "statistics"; // Return the Thymeleaf template
    }

    // API endpoint for JSON data (used by JavaScript)
    @GetMapping("/statistics/json")
    @ResponseBody
    public Map<String, Object> getStatisticsJson() {
        List<Rack> racks = rackService.findAllRacks();

        // Fetch total products and total sold products
        int totalQuantity = racks.stream().mapToInt(Rack::getUsedCapacity).sum();
        int totalSold = orderService.getTotalProductsSold();
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProducts", totalQuantity);
        stats.put("totalSold", totalSold);

        return stats;
    }
}
