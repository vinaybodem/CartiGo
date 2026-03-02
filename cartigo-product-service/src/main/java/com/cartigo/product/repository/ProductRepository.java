package com.cartigo.product.repository;

import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findBySellerId(Long sellerId);

    List<Product> findByCategoryId(Long categoryId);

    boolean existsBySku(String sku);

    List<Product> findByStatus(ProductStatus productStatus);

    List<Product> findByBrandIgnoreCaseAndStatus(String brand, ProductStatus productStatus);
}
