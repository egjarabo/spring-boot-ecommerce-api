package com.egjarabo.ecommerce.product;

import com.egjarabo.ecommerce.AbstractIntegrationTest;
import com.egjarabo.ecommerce.product.dto.ProductRequest;
import com.egjarabo.ecommerce.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // clean state before each test
    }

    @Test
    void shouldCreateProductSuccessfully() {
        var request = new ProductRequest(
                "Laptop Pro", "High performance laptop",
                new BigDecimal("1200.00"), "Electronics", 10);

        var response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/products", request, ProductResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().id());
        assertEquals("Laptop Pro", response.getBody().name());
    }

    @Test
    void shouldReturnAllProducts() {
        // Create two products first
        var request1 = new ProductRequest("Laptop", "Laptop desc",
                new BigDecimal("1200.00"), "Electronics", 10);
        var request2 = new ProductRequest("Mouse", "Mouse desc",
                new BigDecimal("35.00"), "Accessories", 50);

        restTemplate.postForEntity(baseUrl() + "/api/v1/products", request1, ProductResponse.class);
        restTemplate.postForEntity(baseUrl() + "/api/v1/products", request2, ProductResponse.class);

        var response = restTemplate.getForEntity(
                baseUrl() + "/api/v1/products", ProductResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void shouldReturn404WhenProductNotFound() {
        var response = restTemplate.getForEntity(
                baseUrl() + "/api/v1/products/non-existing-id", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldRejectProductWithNegativePrice() {
        var request = new ProductRequest("Laptop", "desc",
                new BigDecimal("-100.00"), "Electronics", 10);

        var response = restTemplate.postForEntity(
                baseUrl() + "/api/v1/products", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}