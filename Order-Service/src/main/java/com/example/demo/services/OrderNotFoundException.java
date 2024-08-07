package com.example.demo.services;

/* custom exception class OrderNotFoundException that extends RuntimeException. This custom exception will be used to indicate that a specific order was not found in your application. Here’s a detailed explanation of what it does and how to use it:

What It Does
Custom Error Handling: The OrderNotFoundException class allows you
to handle cases where an order is not found in a more specific and meaningful way.
 Instead of throwing a generic exception, y
 ou can throw this custom exception to provide more context about the error*/


public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
