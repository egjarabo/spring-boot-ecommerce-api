package com.egjarabo.ecommerce.order.dto;

import com.egjarabo.ecommerce.order.Order;
import com.egjarabo.ecommerce.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String customerId,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items,
        BigDecimal total
) {
    public static OrderResponse from(Order order) {
        var items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        var total = items.stream()
                .map(OrderItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                items,
                total
        );
    }
}
