package com.t2308e.assignment.logservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2308e.assignment.logservice.config.RabitMQConfig;
import com.t2308e.assignment.logservice.model.LogEntity;
import com.t2308e.assignment.logservice.model.LogMessage;
import com.t2308e.assignment.logservice.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(LogConsumer.class);
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public LogConsumer(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabitMQConfig.QUEUE_NAME)
    public void cosumeLogMessage(String message) {
        try {
            logger.info("Received message from RabbitMQ: {}", message);
            LogMessage logMessage = objectMapper.readValue(message, LogMessage.class);

            if (logMessage.getMessage() == null || logMessage.getMessage().trim().isEmpty()) {
                logger.warn("Received log message with null or empty message field: {}", message);
                logMessage.setMessage("Unknown log message");
            }

            LogEntity logEntity = new LogEntity();
            logEntity.setLevel(logMessage.getLevel() != null ? logMessage.getLevel() : "UNKNOWN");
            logEntity.setMessage(logMessage.getMessage());
            logEntity.setTimestamp(logMessage.getTimestamp() != null ? logMessage.getTimestamp() : LocalDateTime.now());
            logEntity.setServiceName(logMessage.getServiceName() != null ? logMessage.getServiceName() : "unknown-service");

            logRepository.save(logEntity);
            logger.info("Saved log to MySQL: {}", logEntity.getMessage());

            if ("ERROR".equalsIgnoreCase(logEntity.getLevel())) {
                logger.warn("Error log detected. Sending notification to tech team: {}", logEntity.getMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to process log message: {}", e.getMessage(), e);
        }
    }
}
