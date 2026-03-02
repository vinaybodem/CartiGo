package com.cartigo.category.exception;

import com.cartigo.category.common.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Object> handleNotFound(ResourceNotFoundException ex){
        return ApiResponse.fail(ex.getMessage());
    }
}
