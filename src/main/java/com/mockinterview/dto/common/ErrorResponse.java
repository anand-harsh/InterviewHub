package com.mockinterview.dto.common;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

// Error Response
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;
}