package com.cartigo.product.controller;

import com.cartigo.product.dto.ProductImageResponse;
import com.cartigo.product.common.ApiResponse;
import com.cartigo.product.entity.ProductImage;
import com.cartigo.product.service.ProductImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/images")
public class ProductImageController {


    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @PostMapping(value="/{productId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductImageResponse> uploadImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "false") Boolean isPrimary
    ) {
        return ApiResponse.ok("Image uploaded",
                service.upload(productId, file, isPrimary));
    }

    @GetMapping("get/{productId}")
    public ApiResponse<List<ProductImage>> getImages(@PathVariable Long productId) {
        return ApiResponse.ok("Images", service.getImages(productId));
    }

    @DeleteMapping("delete/{imageId}")
    public ApiResponse<Object> delete(@PathVariable Long imageId) {
        service.delete(imageId);
        return ApiResponse.ok("Deleted", null);
    }
}
