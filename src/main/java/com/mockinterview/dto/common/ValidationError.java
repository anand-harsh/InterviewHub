package com.mockinterview.dto.common;

import lombok.*;

// Validation Error
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationError {
    private String field;
    private String message;
}