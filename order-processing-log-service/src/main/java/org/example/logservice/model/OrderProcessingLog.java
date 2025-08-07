package org.example.logservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "order_processing_logs")
public class OrderProcessingLog implements Serializable {
    @Id
    private Long orderId;
    private String id;
    private BigDecimal amount;
    private Integer itemsCount;
    private Instant date;
    private String customerName;
    private Long processingTimeMs;

    // Getters and setters
    // ...
}

