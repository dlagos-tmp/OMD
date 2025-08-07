package org.example.logservice.repository;

import org.example.logservice.model.OrderProcessingLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderProcessingLogRepository extends MongoRepository<OrderProcessingLog, String> {
}

