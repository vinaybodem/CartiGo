package com.cartigo.review.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
