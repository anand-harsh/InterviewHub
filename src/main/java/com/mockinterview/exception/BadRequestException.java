package com.mockinterview.exception;

// Bad Request Exception
public class BadRequestException extends MockInterviewException {
    public BadRequestException(String message) {
        super(message);
    }
}