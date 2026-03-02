package com.cartigo.product.service;

import com.cartigo.product.dto.ProductImageResponse;
import com.cartigo.product.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse upload(Long productId, MultipartFile file, Boolean isPrimary);

    List<ProductImage> getImages(Long productId);

    void delete(Long imageId);
}
