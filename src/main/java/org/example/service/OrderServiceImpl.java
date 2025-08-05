package org.example.service;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Order;
import org.example.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Long createOrder(Order order) {
        logger.info("Creating new order for customer: {}", order.getCustomerName());
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getOrderId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        logger.info("Fetching order with id: {}", id);
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        logger.info("Updating order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        order.setCustomerName(orderDetails.getCustomerName());
        order.setStatus(orderDetails.getStatus());

        // Handle order lines update logic if needed

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        logger.info("Deleting order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void processUnprocessedOrders() {
        logger.info("Processing unprocessed orders");
        List<Order> unprocessedOrders = orderRepository.findByStatus("unprocessed");

        if (unprocessedOrders.isEmpty()) {
            logger.info("No unprocessed orders found");
            return;
        }

        logger.info("Found {} unprocessed orders", unprocessedOrders.size());

        for (Order order : unprocessedOrders) {
            order.setStatus("processed");
            orderRepository.save(order);
            logger.info("Updated order {} to processed status", order.getOrderId());
        }
    }
}
