package org.example.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.demo.entities.Product;
import org.example.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ProductService {
    // Logger initialization
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Constructor injection for dependencies
    @Autowired
    public ProductService(ProductRepository productRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return List of products
     * @throws ProductServiceException if there is an error retrieving products
     */
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve all products: " + e.getMessage());
            throw new ProductServiceException("Failed to retrieve all products", e);
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id Product ID
     * @return Product
     * @throws ProductServiceException if the product is not found
     * @throws ProductServiceException  if there is an error retrieving the product
     */
    public Product getProductById(Long id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                return product.get();
            } else {
                LOGGER.warning("Product not found with ID " + id);
                throw new ProductServiceException("Product not found with ID " + id);
            }
        } catch (ProductServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve product by ID: " + e.getMessage());
            throw new ProductServiceException("Failed to retrieve product by ID", e);
        }
    }

    /**
     * Creates a new product in the repository.
     *
     * @param product Product to be created
     * @return Created product
     * @throws ProductServiceException if there is an error creating the product
     */
    public Product createProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            LOGGER.severe("Failed to create product: " + e.getMessage());
            throw new ProductServiceException("Failed to create product", e);
        }
    }

    /**
     * Updates an existing product in the repository.
     *
     * @param id      Product ID
     * @param product Updated product details
     * @return Updated product
     * @throws ProductServiceException if the product is not found
     * @throws ProductServiceException  if there is an error updating the product
     */
    public Product updateProduct(Long id, Product product) {
        try {
            Product existingProduct = getProductById(id);
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setUpdatedAt(product.getUpdatedAt());
            existingProduct.setCreatedAt(product.getCreatedAt());
            existingProduct.setImageUrl(product.getImageUrl());
            existingProduct.setStockQuantity(product.getStockQuantity());
            return productRepository.save(existingProduct);
        } catch (ProductServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to update product: " + e.getMessage());
            throw new ProductServiceException("Failed to update product", e);
        }
    }

    /**
     * Deletes a product from the repository.
     *
     * @param id Product ID
     * @throws ProductServiceException if there is an error deleting the product
     */
    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.severe("Failed to delete product: " + e.getMessage());
            throw new ProductServiceException("Failed to delete product", e);
        }
    }

    /**
     * Kafka listener for product details request
     *
     * @param productIdStr Product ID as a string
     * @throws ProductServiceException if there is an error processing the request
     */
    @KafkaListener(topics = "product-details-request", groupId = "group-3")
    public void receiveProductDetailsRequest(String productIdStr) {
        try {
            Long productId = Long.valueOf(productIdStr);
            Product product = this.getProductById(productId);
            String json = objectMapper.writeValueAsString(product);

            kafkaTemplate.send("product-details-response", json);
        } catch (JsonProcessingException e) {
            LOGGER.severe("Failed to convert product to JSON: " + e.getMessage());
            throw new ProductServiceException("Failed to convert product to JSON", e);
        } catch (Exception e) {
            LOGGER.severe("Failed to handle product-details-request for product ID " + productIdStr + ": " + e.getMessage());
            throw new ProductServiceException("Failed to handle product-details-request", e);
        }
    }
}