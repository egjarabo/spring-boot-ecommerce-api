package com.egjarabo.ecommerce.product.dto;

import com.egjarabo.ecommerce.product.Product;
import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stock
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStock()
        );
    }
}
