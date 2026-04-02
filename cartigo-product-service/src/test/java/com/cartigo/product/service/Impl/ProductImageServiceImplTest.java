package com.cartigo.product.service.Impl;

import com.cartigo.product.dto.ProductImageResponse;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductImage;
import com.cartigo.product.exception.ResourceNotFoundException;
import com.cartigo.product.repository.ProductImageRepository;
import com.cartigo.product.repository.ProductRepository;
import com.cartigo.product.service.Impl.ProductImageServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository imageRepository;

    @InjectMocks
    private ProductImageServiceImpl service;

    // ✅ 1. UPLOAD SUCCESS
    @Test
    void upload_shouldSaveImage() throws Exception {

        Product product = new Product();
        product.setId(1L);

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductImage saved = new ProductImage();
        saved.setId(1L);
        saved.setImageUrl("url");

        when(imageRepository.save(any())).thenReturn(saved);
        when(productRepository.save(any())).thenReturn(product);

        ProductImageResponse response = service.upload(1L, file, true);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    // ❌ 2. PRODUCT NOT FOUND
    @Test
    void upload_shouldThrow_whenProductNotFound() {

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.upload(1L, file, true));
    }

    // ❌ 3. EMPTY FILE
    @Test
    void upload_shouldThrow_whenFileEmpty() {

        Product product = new Product();

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> service.upload(1L, file, true));
    }

    // ✅ 4. GET IMAGES
    @Test
    void getImages_shouldReturnList() {

        when(imageRepository.findByProductId(1L))
                .thenReturn(List.of(new ProductImage(), new ProductImage()));

        List<ProductImage> result = service.getImages(1L);

        assertEquals(2, result.size());
    }

    // ❌ 5. DELETE NOT FOUND
    @Test
    void delete_shouldThrow_whenNotFound() {

        when(imageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.delete(1L));
    }

    // ✅ 6. DELETE SUCCESS
    @Test
    void delete_shouldRemoveImage() {

        ProductImage image = new ProductImage();

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        service.delete(1L);

        verify(imageRepository).delete(image);
    }
}