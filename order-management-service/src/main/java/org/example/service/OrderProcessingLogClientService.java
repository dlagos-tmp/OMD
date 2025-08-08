package org.example.service;

import org.example.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderProcessingLogClientService {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingLogClientService.class);

    @Value("http://order-processing-log-service:8090/logs")
    private String logServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrderLog(Order order) {
        try {
            Map<String, Object> logPayload = new HashMap<>();
            logPayload.put("orderId", order.getOrderId());
            logPayload.put("customerName", order.getCustomerName());
            logPayload.put("date", order.getOrderDate() != null ? order.getOrderDate().atZone(ZoneOffset.UTC).toInstant() : LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
            int itemsCount = order.getOrderLines() != null ? order.getOrderLines().stream().mapToInt(ol -> ol.getQuantity()).sum() : 0;
            logPayload.put("itemsCount", itemsCount);
            BigDecimal amount = order.getOrderLines() != null ? order.getOrderLines().stream().map(ol -> ol.getPrice().multiply(BigDecimal.valueOf(ol.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
            logPayload.put("amount", amount);
            // Optionally add processingTimeMs if available
            ResponseEntity<String> response = restTemplate.postForEntity(logServiceUrl, logPayload, String.class);
            logger.info("Logged order {} to log service. Response: {}", order.getOrderId(), response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to log order {} to log service: {}", order.getOrderId(), e.getMessage());
        }
    }
}

