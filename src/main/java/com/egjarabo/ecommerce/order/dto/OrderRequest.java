package com.egjarabo.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequest(
        @NotBlank(message = "Customer id is required")
        String customerId,

        @NotEmpty(message = "Order must have at least one item")
        List<OrderItemRequest> items
) {}
