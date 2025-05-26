package com.t2308e.assignment.logservice.repository;

import com.t2308e.assignment.logservice.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
