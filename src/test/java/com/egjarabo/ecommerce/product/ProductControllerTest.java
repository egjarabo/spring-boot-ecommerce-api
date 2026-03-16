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

    @Test
    void shouldReturnProductsByCategory() {
        // Create two products in same category and one in different
        restTemplate.postForEntity(baseUrl() + "/api/v1/products",
                new ProductRequest("Laptop", "desc", new BigDecimal("1200.00"), "Electronics", 10),
                ProductResponse.class);
        restTemplate.postForEntity(baseUrl() + "/api/v1/products",
                new ProductRequest("Phone", "desc", new BigDecimal("800.00"), "Electronics", 5),
                ProductResponse.class);
        restTemplate.postForEntity(baseUrl() + "/api/v1/products",
                new ProductRequest("Desk", "desc", new BigDecimal("300.00"), "Furniture", 3),
                ProductResponse.class);

        var response = restTemplate.getForEntity(
                baseUrl() + "/api/v1/products/category/Electronics",
                ProductResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        // First create a product
        var created = restTemplate.postForEntity(
                baseUrl() + "/api/v1/products",
                new ProductRequest("Old Name", "Old desc", new BigDecimal("100.00"), "Electronics", 5),
                ProductResponse.class).getBody();

        // Then update it
        var updateRequest = new ProductRequest("New Name", "New desc",
                new BigDecimal("200.00"), "Electronics", 10);

        var response = restTemplate.exchange(
                baseUrl() + "/api/v1/products/" + created.id(),
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateRequest),
                ProductResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Name", response.getBody().name());
        assertEquals(new BigDecimal("200.00"), response.getBody().price());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingProduct() {
        var updateRequest = new ProductRequest("Name", "desc",
                new BigDecimal("100.00"), "Electronics", 5);

        var response = restTemplate.exchange(
                baseUrl() + "/api/v1/products/non-existing-id",
                org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateRequest),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        var created = restTemplate.postForEntity(
                baseUrl() + "/api/v1/products",
                new ProductRequest("To Delete", "desc", new BigDecimal("50.00"), "Electronics", 1),
                ProductResponse.class).getBody();

        restTemplate.delete(baseUrl() + "/api/v1/products/" + created.id());

        // Verify it's gone
        var response = restTemplate.getForEntity(
                baseUrl() + "/api/v1/products/" + created.id(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingProduct() {
        var response = restTemplate.exchange(
                baseUrl() + "/api/v1/products/non-existing-id",
                org.springframework.http.HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}