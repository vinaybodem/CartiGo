package com.cartigo.product.controller;

import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.common.ApiResponse;
import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductResponse;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.mapper.ProductMapper;
import com.cartigo.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service ) {
        this.service = service;
    }

    @PostMapping("addproduct")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        Product p = service.createProduct(request);
        return ApiResponse.ok("Created", ProductMapper.toResponse(p));
    }

    @GetMapping("get")
    public ApiResponse<List<ProductResponse>> getProductsBySellerId(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) Long sellerId) {
//        if (q != null && !q.isBlank())
//            return ApiResponse.ok("Search results", service.searchProductsByName(q));

        if (sellerId != null){
            List<ProductResponse> out = service.getProductsBySeller(sellerId).stream()
                    .map(ProductMapper::toResponse)
                    .collect(Collectors.toList());
            return ApiResponse.ok("Seller products", out);
        }
        List<ProductResponse> out = service.getAllProducts().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok("All products", out);
    }

    @GetMapping("getproduct/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        return ApiResponse.ok("Product", ProductMapper.toResponse(service.getProductById(id)));
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> listActiveProducts() {
        List<ProductResponse> out = service.listActiveProducts().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok("Active Products ",out);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductResponse>> listByCategory(@PathVariable Long categoryId) {
        List<ProductResponse> out = service.getProductsByCategoryId(categoryId).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok("Products by category ",out);
    }

    // Brand filter
    @GetMapping("/brand/{brand}")
    public ApiResponse<List<ProductResponse>> listByBrand(@PathVariable String brand) {
        List<ProductResponse> out = service.listByBrand(brand).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok("Products By Brand",out);
    }
    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> search(@RequestParam("q") String q) {
        List<ProductResponse> out = service.searchProductsByName(q).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok("searched products",out);
    }
    @PutMapping("update/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest p) {
        return ApiResponse.ok("Updated", ProductMapper.toResponse(service.updateProduct(id, p)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> setStatus(@PathVariable Long id,
                                                     @RequestParam ProductStatus value) {
        return ResponseEntity.ok(ProductMapper.toResponse(service.setStatus(id, value)));
    }
    @DeleteMapping("delete/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        service.deleteProducts(id);
        return ApiResponse.ok("Deleted", null);
    }
}
