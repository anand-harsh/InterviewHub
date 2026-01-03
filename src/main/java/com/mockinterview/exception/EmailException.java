package com.mockinterview.exception;

// Email Exception
public class EmailException extends MockInterviewException {
    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}