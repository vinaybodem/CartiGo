package com.cartigo.product.controller;

import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductResponse;
import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.mapper.ProductMapper;
import com.cartigo.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Seller/Admin create product
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest req) {
        Product p = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductMapper.toResponse(p));
    }

    // Product detail
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(ProductMapper.toResponse(service.get(id)));
    }

    // Home listing
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listActive() {
        List<ProductResponse> out = service.listActive().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Category page like /api/products/category/5  (mobiles categoryId=5)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> listByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> out = service.listByCategory(categoryId).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Brand filter
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> listByBrand(@PathVariable String brand) {
        List<ProductResponse> out = service.listByBrand(brand).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Search by name keyword: /api/products/search?q=mobile
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam("q") String q) {
        List<ProductResponse> out = service.search(q).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Seller/Admin update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody ProductUpdateRequest req) {
        return ResponseEntity.ok(ProductMapper.toResponse(service.update(id, req)));
    }

    // Admin moderation - hide/show
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> setStatus(@PathVariable Long id,
                                                    @RequestParam ProductStatus value) {
        return ResponseEntity.ok(ProductMapper.toResponse(service.setStatus(id, value)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
