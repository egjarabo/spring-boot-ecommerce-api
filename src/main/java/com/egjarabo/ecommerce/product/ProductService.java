package com.egjarabo.ecommerce.product;

import com.egjarabo.ecommerce.common.exception.ResourceNotFoundException;
import com.egjarabo.ecommerce.product.dto.ProductRequest;
import com.egjarabo.ecommerce.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public List<ProductResponse> findAll() {
        return repository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(String id) {
        return repository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public List<ProductResponse> findByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse create(ProductRequest request) {
        var product = new Product(
                null,
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.stock()
        );
        return ProductResponse.from(repository.save(product));
    }

    public ProductResponse update(String id, ProductRequest request) {
        var product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setStock(request.stock());

        return ProductResponse.from(repository.save(product));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
    }
}
