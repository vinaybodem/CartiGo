package com.cartigo.product.service;

import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;

import java.util.List;

public interface ProductService {

    Product createProduct(ProductCreateRequest product);

    Product updateProduct(Long id, ProductUpdateRequest product);

    Product getProductById(Long id);

     List<Product> listActiveProducts();

    List<Product> getAllProducts();

    List<Product> getProductsBySeller(Long sellerId);

    List<Product> getProductsByCategoryId(Long categoryId);

    List<Product> listByBrand(String brand);

    List<Product> searchProductsByName(String keyword);

    void deleteProducts(Long id);

    Product setStatus(Long id, ProductStatus value);
}
