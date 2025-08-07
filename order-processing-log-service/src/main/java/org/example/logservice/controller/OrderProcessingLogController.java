package org.example.logservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logservice.model.OrderProcessingLog;
import org.example.logservice.service.OrderProcessingLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class OrderProcessingLogController {
    private final OrderProcessingLogService logService;
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingLogController.class);
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();


    public OrderProcessingLogController(OrderProcessingLogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<OrderProcessingLog> logOrder(@RequestBody OrderProcessingLog log) {
        OrderProcessingLog saved = logService.saveLog(log);
        try {
            logger.info("Request: {}",objectMapper.writeValueAsString(log));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(saved);
    }
}

