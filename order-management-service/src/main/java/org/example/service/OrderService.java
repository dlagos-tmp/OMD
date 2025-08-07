package org.example.service;

import org.example.model.Order;
import java.util.Optional;
import java.util.List;

public interface OrderService {

    Long createOrder(Order order);

    Optional<Order> getOrderById(Long id);

    Order updateOrder(Long id, Order orderDetails);

    void deleteOrder(Long id);

    List<Order> processUnprocessedOrders();
}
