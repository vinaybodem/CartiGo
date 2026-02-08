package com.cartigo.review.controller;

import com.cartigo.review.dto.ReviewCreateRequest;
import com.cartigo.review.dto.ReviewResponse;
import com.cartigo.review.dto.ReviewUpdateRequest;
import com.cartigo.review.mapper.ReviewMapper;
import com.cartigo.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewMapper.toResponse(service.create(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(ReviewMapper.toResponse(service.get(id)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> listByProduct(@PathVariable Long productId) {
        List<ReviewResponse> out = service.listByProduct(productId).stream()
                .map(ReviewMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest req) {
        return ResponseEntity.ok(ReviewMapper.toResponse(service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
