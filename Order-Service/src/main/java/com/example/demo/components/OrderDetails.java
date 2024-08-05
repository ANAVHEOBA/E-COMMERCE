package com.example.demo.components;

import com.example.demo.entities.Order;

public record OrderDetails (Order order, String productName){}