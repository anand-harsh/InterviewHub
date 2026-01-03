package com.mockinterview.exception;

// Base Exception
public class MockInterviewException extends RuntimeException {
    public MockInterviewException(String message) {
        super(message);
    }

    public MockInterviewException(String message, Throwable cause) {
        super(message, cause);
    }
}