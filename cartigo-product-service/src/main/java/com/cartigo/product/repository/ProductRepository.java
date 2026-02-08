package com.cartigo.product.repository;

import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status);

    List<Product> findByBrandIgnoreCaseAndStatus(String brand, ProductStatus status);

    List<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, ProductStatus status);
}
