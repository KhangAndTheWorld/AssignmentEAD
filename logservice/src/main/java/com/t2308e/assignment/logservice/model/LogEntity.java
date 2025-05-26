package com.t2308e.assignment.logservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "logs")
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String level;
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime timestamp;
    @Column(name = "service_name")
    private String serviceName;
}
