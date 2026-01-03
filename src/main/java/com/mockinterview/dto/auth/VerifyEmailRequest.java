package com.mockinterview.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// Verify Email Request
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyEmailRequest {

    @NotBlank(message = "Token is required")
    private String token;
}