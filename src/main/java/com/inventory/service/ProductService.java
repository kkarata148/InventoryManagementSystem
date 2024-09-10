package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product addProduct(ProductDTO productDTO);

    void addProductsFromCSV(MultipartFile file) throws IOException;

    Product updateProduct(Long id, ProductDTO productDTO);

    ProductDTO convertToDTO(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    void deleteProduct(Long id);

    void sellProduct(Long productId, int quantity, String company);

    // Rack related method
    void addProductToRack(Product product);

    int getTotalProductCount();

    int getTotalSoldProducts();
}
