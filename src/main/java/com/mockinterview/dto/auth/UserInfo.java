package com.mockinterview.dto.auth;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private Long id;
    private String email;
    private Boolean emailVerified;
    private Boolean profileCompleted;
    private Instant lastLogin;
}