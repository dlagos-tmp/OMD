package org.example.logservice.service;

import org.example.logservice.model.OrderProcessingLog;
import org.example.logservice.repository.OrderProcessingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingLogServiceImpl implements OrderProcessingLogService {
    private final OrderProcessingLogRepository repository;

    @Autowired
    public OrderProcessingLogServiceImpl(OrderProcessingLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrderProcessingLog saveLog(OrderProcessingLog log) {
        return repository.save(log);
    }
}

