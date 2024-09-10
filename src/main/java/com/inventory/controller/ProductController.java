package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.model.dto.ProductDTO;
import com.inventory.service.ProductService;
import com.inventory.service.CategoryService;
import com.inventory.service.SoldProductService;
import com.inventory.model.Order;
import com.inventory.model.OrderItem;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductDTO productDTO) {
        productService.addProduct(productDTO);
        return "redirect:/products/list";
    }

    @PostMapping("/upload-csv")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            productService.addProductsFromCSV(file);
            model.addAttribute("successMessage", "Products successfully uploaded!");
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error processing file: " + e.getMessage());
        }
        return "redirect:/products/list";
    }

    @GetMapping("/list")
    public String showInventory(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "inventory/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        ProductDTO productDTO = productService.convertToDTO(productService.getProductById(id));
        model.addAttribute("product", productDTO);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/edit-product";
    }

    @PostMapping("/edit")
    public String updateProduct(@ModelAttribute ProductDTO productDTO) {
        productService.updateProduct(productDTO.getId(), productDTO);
        return "redirect:/products/list";
    }

    @GetMapping("/sold-products")
    public String showSoldProducts(Model model) {
        // Fetch all orders that have been finalized
        List<Order> finalizedOrders = orderService.getAllFinalizedOrders();

        // Extract the sold products from the orders
        List<OrderItem> soldProducts = finalizedOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.toList());

        model.addAttribute("soldProducts", soldProducts);
        return "inventory/sold-products";
    }
    /*
    @GetMapping("/rack-visualization")
    public String showRackVisualization(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "inventory/rack-visualization";
    }*/
}
