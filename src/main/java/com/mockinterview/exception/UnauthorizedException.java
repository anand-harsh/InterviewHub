package com.mockinterview.exception;

// Unauthorized Exception
public class UnauthorizedException extends MockInterviewException {
    public UnauthorizedException(String message) {
        super(message);
    }
}