package com.example.demo.services;


import com.example.demo.components.OrderDetails;
import com.example.demo.entities.Order;
import com.example.demo.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Service
public class OrderService {
    //logger Initialization
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());

    private final OrderRepository orderRepository;
    private KafkaTemplate<String, String> kafkaTemplate = null;
    private CompletableFuture<String> future;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.future = new CompletableFuture<>();
    }
    

    /**
     * Retrieves all orders and includes product details from Kafka.
     * @return List of OrderDetails
     * @throws RuntimeException if there is an error retrieving orders
     */
    public List<OrderDetails> getAllOrders() {
        try {
            // Send a request to Kafka topic to get product details
            kafkaTemplate.send("product-details-request", "1");
            // Wait for the response from Kafka
            String productDetailsJson = future.get(10, TimeUnit.SECONDS);
            // Map orders to OrderDetails including product details
            return orderRepository.findAll().stream()
                    .map(order -> new OrderDetails(order, productDetailsJson))
                    .toList();
        } catch (Exception e) {
            // Log the error and throw a RuntimeException
            // log severe messages that indicate a serious failure in the application.
            // statements to capture important events and exceptions

            LOGGER.severe("Failed to get all orders: " + e.getMessage());
            throw new RuntimeException("Failed to get all orders", e);
        }
    }

    /**
     * Retrieves an order by its ID.
     * @paramid Order ID
     * @return Order
     * @throws OrderNotFoundException if the order is not found
    checks if the request body is valid and returns a 400 Bad Request status if it is not.
    The createOrder method in the service layer
    also validates the order data and throws an IllegalArgumentException
    if the data is invalid,
    which is logged as a warning */
    public Order createOrder(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            LOGGER.warning("Invalid order data: " + order);
            throw new IllegalArgumentException("Order data is invalid");
        }
        try {
            return orderRepository.save(order);
        } catch (Exception e) {
            LOGGER.severe("Failed to create order: " + e.getMessage());
            throw new RuntimeException("Failed to create order", e);
        }
    }


    public Order getOrderById(Long id) {

        try {
            return orderRepository.findById(id).orElseThrow(() -> {
                //  log a warning when the order data is invalid.
                LOGGER.warning("Order not found with ID " + id);
                return new OrderNotFoundException("Order not found with ID " + id);
            });
        } catch (OrderNotFoundException e) {
            LOGGER.warning("OrderNotFoundException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to get order by ID: " + e.getMessage());
            throw new RuntimeException("Failed to get order by ID", e);
        }
    }

    public Order updateOrder(Order order) {
        Long id = 0L;
        Order existingOrder = getOrderById(id);
        existingOrder.setOrderDate(order.getOrderDate());
        existingOrder.setOrderStatus(order.getOrderStatus());
        existingOrder.setItems(order.getItems());

        try {
            return orderRepository.save(existingOrder);
        } catch (Exception e) {
            LOGGER.severe("Failed to update order: " + e.getMessage());
            throw new RuntimeException("Failed to update order", e);
        }
    }

    public void deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.severe("Failed to delete order: " + e.getMessage());
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    @KafkaListener(topics = "product-details-response", groupId = "group-3")
    public String receiveProductDetailsResponse(String json) {
        future.complete(json);
        //logs startup messages, configuration details, or other significant events
        LOGGER.info("Received product details response: " + json);
        return json;
    }
}
