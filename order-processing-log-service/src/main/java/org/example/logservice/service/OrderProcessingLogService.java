package org.example.logservice.service;

import org.example.logservice.model.OrderProcessingLog;

public interface OrderProcessingLogService {
    OrderProcessingLog saveLog(OrderProcessingLog log);
}

