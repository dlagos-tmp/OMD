package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import org.example.model.OrderLine;
import org.example.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateAndGetOrder() throws Exception {
        // Create test order
        String orderJson = "{"
                + "\"customerName\": \"Integration Test Customer\","
                + "\"orderLines\": ["
                + "  {"
                + "    \"productId\": 1,"
                + "    \"quantity\": 2,"
                + "    \"price\": 19.99"
                + "  }"
                + "]"
                + "}";

        // Create order and capture the returned ID
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();

        String orderId = result.getResponse().getContentAsString();

        // Get the created order using the returned ID
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Integration Test Customer"))
                .andExpect(jsonPath("$.status").value("unprocessed"))
                .andExpect(jsonPath("$.orderLines", hasSize(1)))
                .andExpect(jsonPath("$.orderLines[0].productId").value(1))
                .andExpect(jsonPath("$.orderLines[0].quantity").value(2))
                .andExpect(jsonPath("$.orderLines[0].price").value(19.99));
    }

    @Test
    @Transactional
    void testUpdateOrder() throws Exception {
        // Create initial order
        Order order = new Order();
        order.setCustomerName("Original Customer");

        OrderLine orderLine = new OrderLine();
        orderLine.setProductId(1L);
        orderLine.setQuantity(1);
        orderLine.setPrice(new BigDecimal("9.99"));

        order.addOrderLine(orderLine);
        orderRepository.save(order);

        Long orderId = order.getOrderId();

        // Update order
        String updateJson = "{"
                + "\"customerName\": \"Updated Customer\","
                + "\"status\": \"processed\""
                + "}";

        mockMvc.perform(put("/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Updated Customer"))
                .andExpect(jsonPath("$.status").value("processed"));
    }

    @Test
    @Transactional
    void testDeleteOrder() throws Exception {
        // Create order to delete
        Order order = new Order();
        order.setCustomerName("Customer To Delete");
        orderRepository.save(order);

        Long orderId = order.getOrderId();

        // Delete the order
        mockMvc.perform(delete("/orders/" + orderId))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isNotFound());
    }
}
