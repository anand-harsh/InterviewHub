package com.mockinterview.exception;

// Resource Not Found Exception
public class ResourceNotFoundException extends MockInterviewException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}