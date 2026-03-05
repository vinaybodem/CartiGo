package com.cartigo.product.controller;

import com.cartigo.product.dto.ProductImageResponse;
import com.cartigo.product.common.ApiResponse;
import com.cartigo.product.entity.ProductImage;
import com.cartigo.product.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/images")
public class ProductImageController {


    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @PostMapping(value="/{productId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadImage(
            @PathVariable Long productId,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "false") Boolean isPrimary
    ) {
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Image uploaded",
                service.upload(productId, file, isPrimary)),HttpStatus.CREATED);
    }

    @GetMapping("get/{productId}")
    public ResponseEntity<ApiResponse<?>> getImages(@PathVariable Long productId) {
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Images", service.getImages(productId)),HttpStatus.OK);
    }

    @DeleteMapping("delete/{imageId}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long imageId) {
        service.delete(imageId);
        return new ResponseEntity<ApiResponse<?>>(ApiResponse.ok("Deleted", null),HttpStatus.NO_CONTENT);
    }
}
