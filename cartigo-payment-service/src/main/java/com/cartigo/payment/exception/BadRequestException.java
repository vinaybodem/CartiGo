package com.cartigo.payment.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
