package org.example.logservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "order_processing_logs")
public class OrderProcessingLog {
    @Id
    private String id;
    private Long orderId;
    private BigDecimal amount;
    private Integer itemsCount;
    private Instant date;
    private String customerName;
    private Long processingTimeMs;

    // Getters and setters
    // ...
}

