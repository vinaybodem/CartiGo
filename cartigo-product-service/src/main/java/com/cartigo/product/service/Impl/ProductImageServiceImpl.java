package com.cartigo.product.service.Impl;

import com.cartigo.product.dto.ProductImageResponse;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductImage;
import com.cartigo.product.exception.ResourceNotFoundException;
import com.cartigo.product.repository.ProductImageRepository;
import com.cartigo.product.repository.ProductRepository;
import com.cartigo.product.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;

    public ProductImageServiceImpl(ProductRepository productRepository, ProductImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    private static final String UPLOAD_DIR = "uploads/products/";
    @Override
    public ProductImageResponse upload(Long productId, MultipartFile file, Boolean isPrimary) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        try {

            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            ProductImage image = new ProductImage();
            image.setImageUrl("/" + UPLOAD_DIR + fileName);
            image.setPrimary(isPrimary);
            image.setProduct(product);

            ProductImage saved = imageRepository.save(image);

            product.setImage(saved);
            productRepository.save(product);

            return new ProductImageResponse(
                    saved.getId(),
                    saved.getImageUrl(),
                    saved.getPrimary()
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Image upload failed", e);
        }
    }

    @Override
    public List<ProductImage> getImages(Long productId) {
        return imageRepository.findByProductId(productId);
    }

    @Override
    public void delete(Long imageId) {
        ProductImage image = imageRepository.findById(imageId).orElseThrow(()->new RuntimeException("Image Not Found"));
        imageRepository.delete(image);
    }
}
