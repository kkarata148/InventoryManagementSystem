package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.model.Rack;
import com.inventory.service.RackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/racks")
public class RackController {

    @Autowired
    private RackService rackService;

    @GetMapping("/visualization")
    public String showRackVisualization(Model model) {
        List<Rack> racks = rackService.findAllRacks();
        for (Rack rack : racks) {
            System.out.println("Rack: " + rack.getName() + ", Products: " + rack.getProducts().size());
        }
        model.addAttribute("racks", racks);
        return "inventory/rack-visualization";
    }



    @GetMapping("/add")
    public String showAddRackForm(Model model) {
        model.addAttribute("rack", new Rack());
        return "inventory/add-rack";
    }

    @PostMapping("/add")
    public String createRack(@ModelAttribute Rack rack) {
        rackService.saveRack(rack);
        return "redirect:/racks/visualization";
    }
}
