package com.egjarabo.ecommerce.order;

import com.egjarabo.ecommerce.AbstractIntegrationTest;
import com.egjarabo.ecommerce.order.dto.OrderItemRequest;
import com.egjarabo.ecommerce.order.dto.OrderRequest;
import com.egjarabo.ecommerce.order.dto.OrderResponse;
import com.egjarabo.ecommerce.product.ProductRepository;
import com.egjarabo.ecommerce.product.dto.ProductRequest;
import com.egjarabo.ecommerce.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderControllerTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    private ProductResponse createProduct() {
        var request = new ProductRequest("Laptop Pro", "High performance laptop",
                new BigDecimal("1200.00"), "Electronics", 10);
        return restTemplate.postForEntity(
                baseUrl() + "/api/v1/products", request, ProductResponse.class).getBody();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        var product = createProduct();
        var request = new OrderRequest(
                "customer-01",
                List.of(new OrderItemRequest(product.id(), 2)));

        var response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/orders", request, OrderResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().id());
        assertEquals(OrderStatus.PENDING, response.getBody().status());
        assertEquals(1, response.getBody().items().size());
        assertEquals(new BigDecimal("2400.00"), response.getBody().total());
    }

    @Test
    void shouldSnapshotProductDataOnOrderCreation() {
        var product = createProduct();
        var request = new OrderRequest(
                "customer-01",
                List.of(new OrderItemRequest(product.id(), 1)));

        var response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/orders", request, OrderResponse.class);

        // Verify snapshot — product name and price captured at order time
        var item = response.getBody().items().get(0);
        assertEquals("Laptop Pro", item.productName());
        assertEquals(new BigDecimal("1200.00"), item.price());
    }

    @Test
    void shouldUpdateOrderStatus() {
        var product = createProduct();
        var orderRequest = new OrderRequest(
                "customer-01",
                List.of(new OrderItemRequest(product.id(), 1)));

        var order = restTemplate.postForEntity(
                baseUrl() + "/api/v1/orders", orderRequest, OrderResponse.class).getBody();

        var response = restTemplate.exchange(
                baseUrl() + "/api/v1/orders/" + order.id() + "/status?status=CONFIRMED",
                org.springframework.http.HttpMethod.PATCH,
                null,
                OrderResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.CONFIRMED, response.getBody().status());
    }

    @Test
    void shouldReturn404ForNonExistingOrder() {
        var response = restTemplate.getForEntity(
                baseUrl() + "/api/v1/orders/non-existing-id", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
