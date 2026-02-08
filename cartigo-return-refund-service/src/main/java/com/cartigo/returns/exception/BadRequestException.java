package com.cartigo.returns.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
