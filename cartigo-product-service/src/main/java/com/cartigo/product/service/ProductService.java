package com.cartigo.product.service;

import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.exception.BadRequestException;
import com.cartigo.product.exception.ResourceNotFoundException;
import com.cartigo.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(ProductCreateRequest req) {
        if (repo.existsBySku(req.getSku())) {
            throw new BadRequestException("SKU already exists");
        }

        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setDiscountPrice(req.getDiscountPrice());
        p.setBrand(req.getBrand());
        p.setSku(req.getSku());
        p.setImageUrl(req.getImageUrl());
        p.setCategoryId(req.getCategoryId());
        p.setSellerId(req.getSellerId());
        p.setStockQuantity(req.getStockQuantity());
        p.setStatus(ProductStatus.ACTIVE);

        return repo.save(p);
    }

    // ✅ Public get: only ACTIVE
    public Product get(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (p.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        return p;
    }

    public List<Product> listActive() {
        return repo.findByStatus(ProductStatus.ACTIVE);
    }

    public List<Product> listByCategory(Long categoryId) {
        return repo.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE);
    }

    public List<Product> listByBrand(String brand) {
        return repo.findByBrandIgnoreCaseAndStatus(brand, ProductStatus.ACTIVE);
    }

    public List<Product> search(String keyword) {
        return repo.findByNameContainingIgnoreCaseAndStatus(keyword, ProductStatus.ACTIVE);
    }

    public Product update(Long id, ProductUpdateRequest req) {
        // ✅ Admin/Seller update should work even if inactive
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (req.getName() != null) p.setName(req.getName());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getDiscountPrice() != null) p.setDiscountPrice(req.getDiscountPrice());
        if (req.getBrand() != null) p.setBrand(req.getBrand());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getCategoryId() != null) p.setCategoryId(req.getCategoryId());
        if (req.getStockQuantity() != null) p.setStockQuantity(req.getStockQuantity());

        return repo.save(p);
    }

    public Product setStatus(Long id, ProductStatus status) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        p.setStatus(status);
        return repo.save(p);
    }

    public void delete(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        repo.delete(p);
    }
}
