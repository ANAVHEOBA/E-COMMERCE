package com.example.demo;

// Class representing an array
public class Array {
    private int[] items; // Array to hold items

    // Constructor to initialize the array with a given length
    public Array(int length) {
        this.items = new int[length];
    }

    // Method to print the items in the array
    public void print() {
        for(int item : items) {
            System.out.println(item);
        }
    }
}



