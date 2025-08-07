package org.example.scheduler;

import org.example.service.OrderService;
import org.example.service.OrderProcessingLogClientService;
import org.example.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProcessingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingScheduler.class);

    private final OrderService orderService;
    private final OrderProcessingLogClientService logClientService;

    @Autowired
    public OrderProcessingScheduler(OrderService orderService, OrderProcessingLogClientService logClientService) {
        this.orderService = orderService;
        this.logClientService = logClientService;
    }

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void processOrders() {
        logger.info("Starting scheduled order processing task");
        try {
            List<Order> processedOrders = orderService.processUnprocessedOrders();
            for (Order order : processedOrders) {
                logClientService.sendOrderLog(order);
            }
            logger.info("Completed scheduled order processing task");
        } catch (Exception e) {
            logger.error("Error during scheduled order processing: {}", e.getMessage(), e);
        }
    }
}
