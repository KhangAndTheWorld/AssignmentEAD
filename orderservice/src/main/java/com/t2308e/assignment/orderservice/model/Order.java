package com.t2308e.assignment.orderservice.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String id;
    private Long customerId;
    private List<Item> items;
    private String paymentMethod;
    private String creditCardNumber;
    private double totalAmount;
    private String status;
    private String errorMessage;
}
