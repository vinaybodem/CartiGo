package com.cartigo.order.exception;


import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<String> handleFeignBadRequest(FeignException.BadRequest ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.contentUTF8());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}