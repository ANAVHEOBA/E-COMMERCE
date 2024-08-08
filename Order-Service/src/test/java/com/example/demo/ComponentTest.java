package com.example.demo;

/* unit Testing:- The ComponentTest class is testing the Array class by
creating an instance of it and calling its print method.
This test checks if the Array class initializes correctly and if the
print method outputs the array elements as expected.

 This code helps ensure that the Array class works as expected. By creating an instance and calling its methods,
 we can verify that the class behaves correctly*/

public class ComponentTest {
    public static void main(String[] args) {
        Array array = new Array(5);
        array.print();
    }
}