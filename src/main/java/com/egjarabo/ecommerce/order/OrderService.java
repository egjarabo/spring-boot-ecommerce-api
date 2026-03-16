package com.egjarabo.ecommerce.order;

import com.egjarabo.ecommerce.common.exception.ResourceNotFoundException;
import com.egjarabo.ecommerce.order.dto.*;
import com.egjarabo.ecommerce.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse findById(String id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    public List<OrderResponse> findByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        var order = new Order();
        order.setCustomerId(request.customerId());

        // Build items with product snapshot
        var items = request.items().stream()
                .map(itemRequest -> buildOrderItem(itemRequest, order))
                .toList();

        order.setItems(items);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(String id, OrderStatus newStatus) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setStatus(newStatus);
        return OrderResponse.from(orderRepository.save(order));
    }

    private OrderItem buildOrderItem(OrderItemRequest request, Order order) {
        var product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + request.productId()));

        var item = new OrderItem();
        item.setOrder(order);
        item.setProductId(product.getId());
        item.setProductName(product.getName()); // snapshot
        item.setPrice(product.getPrice());      // snapshot
        item.setQuantity(request.quantity());
        return item;
    }
}
