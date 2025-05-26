package com.t2308e.assignment.orderservice.service;

import com.t2308e.assignment.orderservice.config.RabbitMQConfig;
import com.t2308e.assignment.orderservice.model.LogMessage;
import com.t2308e.assignment.orderservice.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OrderService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public Order createOrder(Order order) {
        String orderId = UUID.randomUUID().toString();
        order.setId(orderId);
        logger.info("Processing order with ID: {}", orderId);

        double totalAmount = order.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        LogMessage logMessage = createLogMessage(order, orderId, totalAmount);

        try {
            String jsonMessage = objectMapper.writeValueAsString(logMessage);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", jsonMessage);
            logger.info("Sent log to RabbitMQ: {}", jsonMessage);
        } catch (Exception e) {
            logger.error("Failed to send log to RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send log to RabbitMQ", e);
        }

        if ("ERROR".equals(logMessage.getLevel())) {
            throw new IllegalArgumentException(logMessage.getMessage());
        }

        return order;
    }

    private LogMessage createLogMessage(Order order, String orderId, double totalAmount) {
        LogMessage logMessage = new LogMessage();
        logMessage.setTimestamp(LocalDateTime.now());
        logMessage.setServiceName("order-service");

        if (!"creditCard".equalsIgnoreCase(order.getPaymentMethod())) {
            logMessage.setLevel("ERROR");
            logMessage.setMessage(String.format("Payment failed for order ID: %s. Reason: Invalid payment method", orderId));
            return logMessage;
        }

        String cardNumber = order.getCreditCardNumber() != null
                ? order.getCreditCardNumber().replaceAll("[^0-9]", "")
                : "";
        if (cardNumber.length() != 16) {
            logMessage.setLevel("ERROR");
            logMessage.setMessage(String.format("Payment failed for order ID: %s. Reason: Invalid credit card number", orderId));
            return logMessage;
        }

        logMessage.setLevel("INFO");
        logMessage.setMessage(String.format("Order created successfully. Order ID: %s, Customer ID: %s, Total amount: %.2f",
                orderId, order.getCustomerId(), totalAmount));
        return logMessage;
    }
}
