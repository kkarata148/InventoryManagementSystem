package com.inventory.service.impl;

import com.inventory.model.*;
import com.inventory.model.dto.ProductDTO;
import com.inventory.repository.*;
import com.inventory.service.ProductService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SoldProductRepository soldProductRepository;
    private final RackRepository rackRepository;
    private final ProductRackRepository productRackRepository;
    private final RackGapRepository rackGapRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, SoldProductRepository soldProductRepository, RackRepository rackRepository, ProductRackRepository productRackRepository, RackGapRepository rackGapRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.soldProductRepository = soldProductRepository;
        this.rackRepository = rackRepository;
        this.productRackRepository = productRackRepository;
        this.rackGapRepository = rackGapRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Product addProduct(ProductDTO productDTO) {
        // Generate a unique SKU for the product
        String generatedSku = generateSKU(productDTO.getName());

        // Check if the SKU already exists
        Optional<Product> existingProduct = productRepository.findBySku(generatedSku);
        if (existingProduct.isPresent()) {
            throw new DuplicateSKUException("Product with SKU " + generatedSku + " already exists.");
        }

        // Create and populate a new product entity
        Product product = new Product();
        product.setSku(generatedSku);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setStatus(productDTO.getQuantity() > 0 ? "Available" : "Out of Stock");
        product.setPrice(productDTO.getPrice());

        // Retrieve and set the category
        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        product.setCategory(category);

        // Add product to the rack
        addProductToRack(product);

        // Save the product in the repository
        return productRepository.save(product);
    }


    @Override
    @Transactional
    public void addProductsFromCSV(MultipartFile file) throws IOException {
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withSkipLines(1).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 7) {
                    System.out.println("Skipping line due to insufficient columns: " + Arrays.toString(line));
                    continue;
                }
                try {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setSku(line[0]);
                    productDTO.setName(line[1]);
                    productDTO.setDescription(line[2]);
                    productDTO.setQuantity(Integer.parseInt(line[3]));
                    productDTO.setCategoryId(Long.parseLong(line[4]));
                    productDTO.setPrice(Double.parseDouble(line[5]));
                    productDTO.setStatus(line[6]);

                    Product product = this.addProduct(productDTO);
                    System.out.println("Saved Product: " + product.getName() + " with SKU: " + product.getSku());
                } catch (Exception e) {
                    System.out.println("Error processing line: " + Arrays.toString(line));
                    e.printStackTrace();
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid CSV format.", e);
        }
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        product.setSku(productDTO.getSku());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setStatus(productDTO.getQuantity() > 0 ? "Available" : "Out of Stock");
        product.setPrice(productDTO.getPrice());

        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setSku(product.getSku());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setStatus(product.getStatus());
        productDTO.setId(product.getId());  // Make sure to add this in ProductDTO
        return productDTO;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void sellProduct(Long productId, int quantity, String company) {
        Product product = getProductById(productId);

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient quantity in stock");
        }

        // Reduce the product quantity
        product.setQuantity(product.getQuantity() - quantity);

        if (product.getQuantity() == 0) {
            product.setStatus("Out of Stock");
        }

        // Save the updated product
        productRepository.save(product);

        // Log or save the sale information in a SoldProduct entity
        SoldProduct soldProduct = new SoldProduct();
        soldProduct.setName(product.getName());
        soldProduct.setSku(product.getSku());
        soldProduct.setSoldDate(LocalDateTime.now());
        soldProduct.setCompany(company);
        soldProduct.setQuantity(quantity);

        soldProductRepository.save(soldProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProductToRack(Product product) {
        List<Rack> availableRacks = rackRepository.findAllWithFreeSpace(); // Fetches racks with products eagerly
        int remainingQuantity = product.getQuantity();
        if (product.getSku() != null) {
            this.productRepository.save(product);
        }
        try {

            for (Rack rack : availableRacks) {
                if (remainingQuantity == 0) break;

                if (rack.isEmpty()) {
                    remainingQuantity = addInEmptyRack(product, rack, remainingQuantity);
                } else {
                    remainingQuantity = addInGapsOfRack(product, rack, remainingQuantity);

                    remainingQuantity = addAsLastAtRack(product, rack, remainingQuantity);
                }
                this.rackRepository.save(rack);
            }

            if (remainingQuantity > 0) {
                throw new IllegalArgumentException("Not enough space in available racks to store the entire product.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private int addInEmptyRack(Product product, Rack rack, int remainingQuantity) {
        int quantityToAssign = Math.min(remainingQuantity, rack.getTotalCapacity());
        ProductRack productRack = new ProductRack(product, rack, 1, quantityToAssign);
        this.productRackRepository.save(productRack);

        rack.setUsedCapacity(quantityToAssign);
        remainingQuantity -= quantityToAssign;
        return remainingQuantity;
    }

    private int addInGapsOfRack(Product product, Rack rack, int remainingQuantity) {
        Iterator<RackGap> gaps = rack.getGaps().iterator();
        while (gaps.hasNext()) {
            RackGap gap = gaps.next();
            if (remainingQuantity == 0) break;

            int gapSize = gap.getEndPosition() - gap.getStartPosition() + 1;
            int quantityToAssign = Math.min(remainingQuantity, gapSize);

            int start = gap.getStartPosition();
            int end = start + quantityToAssign - 1;

            // Create and save the ProductRack entry for the assigned quantity
            ProductRack productRack = new ProductRack(product, rack, start, end);
            this.productRackRepository.save(productRack);

            // Update the used capacity of the rack
            rack.setUsedCapacity(rack.getUsedCapacity() + quantityToAssign);
            remainingQuantity -= quantityToAssign;

            // If the gap is fully filled, remove the gap
            if (quantityToAssign == gapSize) {
                gaps.remove();
            } else {
                // Adjust the gap start position to reflect the newly assigned space
                gap.setStartPosition(end + 1);
            }
        }

        return remainingQuantity;
    }

    private int addAsLastAtRack(Product product, Rack rack, int remainingQuantity) {
        int freeCapacity = rack.getTotalCapacity() - rack.getUsedCapacity();
        if (freeCapacity > 0 && remainingQuantity > 0) {
            int quantityToAssign = Math.min(remainingQuantity, freeCapacity);
            int start = rack.getUsedCapacity() + 1;
            int end = start + quantityToAssign - 1;

            ProductRack productRack = new ProductRack(product, rack, start, end);
            this.productRackRepository.save(productRack);

            rack.setUsedCapacity(rack.getUsedCapacity() + quantityToAssign);
            remainingQuantity -= quantityToAssign;
        }
        return remainingQuantity;
    }

    public class DuplicateSKUException extends RuntimeException {
        public DuplicateSKUException(String message) {
            super(message);
        }
    }


    @Transactional
    public String generateSKU(String productName) {
        // Append current time in milliseconds to make SKU unique
        return productName.toUpperCase().replaceAll("\\s+", "_") + "_" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public int getTotalProductCount() {
        return productRepository.findAll().size();
    }

    @Override
    @Transactional
    public int getTotalSoldProducts() {
        return soldProductRepository.findAll().stream().mapToInt(SoldProduct::getQuantity).sum();
    }

    @Override
    public List<Product> getAllAvailableProducts() {
        return this.productRepository.findAllByQuantityGreaterThan(0);
    }
}
