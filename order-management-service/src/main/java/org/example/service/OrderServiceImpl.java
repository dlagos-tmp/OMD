package org.example.service;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Order;
import org.example.model.OrderLine;
import org.example.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Long createOrder(Order order) {
        logger.info("Creating new order for customer: {}", order.getCustomerName());
        // Setup bidirectional relationship for each order line
        order.getOrderLines().forEach(orderLine -> {
            orderLine.setOrder(order);
        });
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

        if(orderDetails.getCustomerName() != null || !orderDetails.getCustomerName().isEmpty()) {
            order.setCustomerName(orderDetails.getCustomerName());
        }

        if(orderDetails.getStatus() != null && !orderDetails.getStatus().isEmpty()){
            order.setStatus(orderDetails.getStatus());
        }

        // Collect order lines to remove in a separate list
        List<OrderLine> linesToRemove = new ArrayList<>();
        for (OrderLine dbOrderLine : order.getOrderLines()) {
            boolean notExistInDB = orderDetails.getOrderLines().stream()
                    .filter(requestdOrderLine -> requestdOrderLine.getId() != null)
                    .noneMatch(requestdOrderLine ->
                            dbOrderLine.getId().equals(requestdOrderLine.getId()));
            if (notExistInDB) {
                linesToRemove.add(dbOrderLine);
            }
        }
        order.getOrderLines().removeAll(linesToRemove);

        // Handle order lines update logic if needed
        orderDetails.getOrderLines().forEach(requestdOrderLine -> {
            if(requestdOrderLine.getId() == null) {
                // If the order line ID is null, it means it's a new order line
                //requestdOrderLine.setOrder(order);
                requestdOrderLine.setOrderId(order.getOrderId());
                order.getOrderLines().add(requestdOrderLine);

            } else {
                // If it exists, update it
              Optional<OrderLine> optLine =  order.getOrderLines().stream()
                        .filter(dbOrderLine -> dbOrderLine.getId().equals(requestdOrderLine.getId())).findFirst();
                // Update the existing order line
              optLine.ifPresent(dbOrderLine -> {
                    dbOrderLine.setProductId(requestdOrderLine.getProductId());
                    dbOrderLine.setQuantity(requestdOrderLine.getQuantity());
                    dbOrderLine.setPrice(requestdOrderLine.getPrice());
                });

            }
        });


        //entityManager.flush();
        // Remove after iteration
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
    public List<Order> processUnprocessedOrders() {
        logger.info("Processing unprocessed orders");
        List<Order> unprocessedOrders = orderRepository.findByStatus("unprocessed");

        if (unprocessedOrders.isEmpty()) {
            logger.info("No unprocessed orders found");
            return unprocessedOrders;
        }

        logger.info("Found {} unprocessed orders", unprocessedOrders.size());

        for (Order order : unprocessedOrders) {
            order.setStatus("processed");
            orderRepository.save(order);
            logger.info("Updated order {} to processed status", order.getOrderId());
        }
        return unprocessedOrders;
    }
}
