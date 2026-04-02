package com.cartigo.product.service;

import com.cartigo.product.client.CategoryClient;
import com.cartigo.product.client.SellerClient;
import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.dto.SellerDto;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.exception.BadRequestException;
import com.cartigo.product.exception.ResourceNotFoundException;
import com.cartigo.product.repository.ProductRepository;
import com.cartigo.product.service.Impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryClient categoryClient;

    @Mock
    private SellerClient sellerClient;

    @InjectMocks
    private ProductServiceImpl productService;

    // ✅ 1. CREATE SUCCESS
    @Test
    void createProduct_shouldCreateSuccessfully() {

        ProductCreateRequest req = new ProductCreateRequest();
        req.setName("Phone");
        req.setDescription("Nice phone");
        req.setPrice(BigDecimal.valueOf(1000));
        req.setBrand("Samsung");
        req.setSku("SKU123");
        req.setCategoryId(1L);
        req.setSellerId(1L);

        when(sellerClient.getSellerById(1L)).thenReturn(new SellerDto());
        when(categoryClient.isCategoryValid(1L)).thenReturn(true);
        when(productRepository.existsBySku("SKU123")).thenReturn(false);

        Product saved = new Product();
        saved.setId(1L);

        when(productRepository.save(any())).thenReturn(saved);

        Product result = productService.createProduct(req);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // ❌ 2. INVALID SELLER
    @Test
    void createProduct_shouldThrow_whenInvalidSeller() {

        ProductCreateRequest req = new ProductCreateRequest();
        req.setSellerId(1L);

        when(sellerClient.getSellerById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> productService.createProduct(req));
    }

    // ❌ 3. INVALID CATEGORY
    @Test
    void createProduct_shouldThrow_whenInvalidCategory() {

        ProductCreateRequest req = new ProductCreateRequest();
        req.setSellerId(1L);
        req.setCategoryId(1L);

        when(sellerClient.getSellerById(1L)).thenReturn(new SellerDto());
        when(categoryClient.isCategoryValid(1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> productService.createProduct(req));
    }

    // ❌ 4. DUPLICATE SKU
    @Test
    void createProduct_shouldThrow_whenSkuExists() {

        ProductCreateRequest req = new ProductCreateRequest();
        req.setSellerId(1L);
        req.setCategoryId(1L);
        req.setSku("SKU123");

        when(sellerClient.getSellerById(1L)).thenReturn(new SellerDto());
        when(categoryClient.isCategoryValid(1L)).thenReturn(true);
        when(productRepository.existsBySku("SKU123")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> productService.createProduct(req));
    }

    // ❌ 5. PRODUCT NOT FOUND
    @Test
    void getProductById_shouldThrow_whenNotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    // ❌ 6. PRODUCT INACTIVE
    @Test
    void getProductById_shouldThrow_whenInactive() {

        Product p = new Product();
        p.setStatus(ProductStatus.INACTIVE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));
    }

    // ✅ 7. GET SUCCESS
    @Test
    void getProductById_shouldReturnProduct() {

        Product p = new Product();
        p.setId(1L);
        p.setStatus(ProductStatus.ACTIVE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Product result = productService.getProductById(1L);

        assertEquals(1L, result.getId());
    }

    // ✅ 8. UPDATE PRODUCT
    @Test
    void updateProduct_shouldUpdateFields() {

        Product p = new Product();
        p.setId(1L);
        p.setStatus(ProductStatus.ACTIVE);

        ProductUpdateRequest req = new ProductUpdateRequest();
        req.setName("Updated");

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productRepository.save(any())).thenReturn(p);

        Product result = productService.updateProduct(1L, req);

        assertEquals("Updated", result.getName());
    }

    // ❌ 9. CATEGORY PRODUCTS NOT FOUND
    @Test
    void getProductsByCategoryId_shouldThrow_whenEmpty() {

        when(categoryClient.getCategroyId("mobile")).thenReturn(1L);
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductsByCategoryId("mobile"));
    }

    // ❌ 10. BRAND NOT FOUND
    @Test
    void listByBrand_shouldThrow_whenEmpty() {

        when(productRepository.findByBrandIgnoreCaseAndStatus("apple", ProductStatus.ACTIVE))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.listByBrand("apple"));
    }

    // ✅ 11. DELETE PRODUCT
    @Test
    void deleteProducts_shouldDelete() {

        Product p = new Product();
        p.setStatus(ProductStatus.ACTIVE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        productService.deleteProducts(1L);

        verify(productRepository).delete(p);
    }

    // ✅ 12. SET STATUS
    @Test
    void setStatus_shouldUpdate() {

        Product p = new Product();

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productRepository.save(any())).thenReturn(p);

        Product result = productService.setStatus(1L, ProductStatus.INACTIVE);

        assertEquals(ProductStatus.INACTIVE, result.getStatus());
    }
}