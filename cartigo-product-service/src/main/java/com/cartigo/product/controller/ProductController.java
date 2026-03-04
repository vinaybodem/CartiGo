package com.cartigo.product.controller;

import com.cartigo.product.dto.ProductUpdateRequest;
import com.cartigo.product.common.ApiResponse;
import com.cartigo.product.dto.ProductCreateRequest;
import com.cartigo.product.dto.ProductResponse;
import com.cartigo.product.dto.ResponseToInvetory;
import com.cartigo.product.entity.Product;
import com.cartigo.product.entity.ProductStatus;
import com.cartigo.product.mapper.ProductMapper;
import com.cartigo.product.service.ProductService;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody ProductCreateRequest request) {
        Product p = service.createProduct(request);
//        return ApiResponse.ok("Created", ProductMapper.toResponse(p));
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Created", ProductMapper.toResponse(p)), HttpStatus.CREATED);
    }

    @GetMapping("get")
    public ResponseEntity<ApiResponse<?>> getProductsBySellerId(@RequestParam(required = false) String q,
                                                                @RequestParam(required = false) Long sellerId) {
//        if (q != null && !q.isBlank())
//            return ApiResponse.ok("Search results", service.searchProductsByName(q));

        if (sellerId != null){
            List<ProductResponse> out = service.getProductsBySeller(sellerId).stream()
                    .map(ProductMapper::toResponse)
                    .collect(Collectors.toList());
            return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Seller products", out), HttpStatus.OK);
        }
        List<ProductResponse> out = service.getAllProducts().stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("All products", out),HttpStatus.OK);
    }

    @GetMapping("getproduct/{id}")
    public ResponseEntity<ApiResponse<?>> getProduct(@PathVariable Long id) {
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Product", ProductMapper.toResponse(service.getProductById(id))),HttpStatus.OK);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<ApiResponse<?>> listByCategory(@PathVariable String categoryName) {
        List<ProductResponse> out = service.getProductsByCategoryId(categoryName).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Products by category ",out),HttpStatus.OK);
    }

    // Brand filter
    @GetMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse<?>> listByBrand(@PathVariable String brand) {
        List<ProductResponse> out = service.listByBrand(brand).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Products By Brand",out),HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> search(@RequestParam("q") String q) {
        List<ProductResponse> out = service.searchProductsByName(q).stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ApiResponse.ok("searched products",out),HttpStatus.OK);
    }
    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest p) {
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Updated", ProductMapper.toResponse(service.updateProduct(id, p))),HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<?>> setStatus(@PathVariable Long id,
                                                     @RequestParam ProductStatus value) {
        return new ResponseEntity<>(ApiResponse.ok("Status Changed",ProductMapper.toResponse(service.setStatus(id, value))),HttpStatus.OK);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        service.deleteProducts(id);
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Deleted", null),HttpStatus.NO_CONTENT);
    }

    @GetMapping("/validate/{pord_id}")
    public ResponseToInvetory validate(@PathVariable Long pord_id) {
        return  service.getProductIdAndSellerId(pord_id);
    }
}
