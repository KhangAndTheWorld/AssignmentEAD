package com.t2308e.assignment.orderservice.model;

import lombok.Data;

@Data
public class Item {
    private Long productId;
    private Integer quantity;
    private Double price;
}
