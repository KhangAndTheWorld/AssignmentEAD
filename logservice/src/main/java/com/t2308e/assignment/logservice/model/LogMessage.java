package com.t2308e.assignment.logservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogMessage {
    private String level;
    private String message;
    private LocalDateTime timestamp;
    private String serviceName;
}
