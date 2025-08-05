package org.example.repository;

import org.example.model.Order;
import org.example.model.OrderLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testSaveAndFindById() {
        // Create and save an order
        Order order = new Order();
        order.setCustomerName("Test Customer");
        order.setStatus("unprocessed");

        OrderLine orderLine = new OrderLine();
        orderLine.setProductId(101L);
        orderLine.setQuantity(2);
        orderLine.setPrice(new BigDecimal("29.99"));

        order.addOrderLine(orderLine);

        Order savedOrder = orderRepository.save(order);

        // Find by ID and verify
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getOrderId());

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getCustomerName()).isEqualTo("Test Customer");
        assertThat(foundOrder.get().getStatus()).isEqualTo("unprocessed");
        assertThat(foundOrder.get().getOrderLines()).hasSize(1);
        assertThat(foundOrder.get().getOrderLines().get(0).getProductId()).isEqualTo(101L);
    }

    @Test
    void testFindByStatus() {
        // Create and save multiple orders with different statuses
        Order order1 = new Order();
        order1.setCustomerName("Customer 1");
        order1.setStatus("unprocessed");
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomerName("Customer 2");
        order2.setStatus("processed");
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setCustomerName("Customer 3");
        order3.setStatus("unprocessed");
        orderRepository.save(order3);

        // Find by status and verify
        List<Order> unprocessedOrders = orderRepository.findByStatus("unprocessed");
        List<Order> processedOrders = orderRepository.findByStatus("processed");

        assertThat(unprocessedOrders).hasSize(2);
        assertThat(processedOrders).hasSize(1);

        assertThat(unprocessedOrders).extracting("customerName")
            .containsExactlyInAnyOrder("Customer 1", "Customer 3");
        assertThat(processedOrders).extracting("customerName")
            .containsExactly("Customer 2");
    }

    @Test
    void testDeleteOrder() {
        // Create and save an order
        Order order = new Order();
        order.setCustomerName("Customer to delete");
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getOrderId();

        // Verify it exists
        assertThat(orderRepository.findById(orderId)).isPresent();

        // Delete and verify
        orderRepository.delete(savedOrder);
        assertThat(orderRepository.findById(orderId)).isEmpty();
    }
}

