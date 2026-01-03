package com.mockinterview.controller;

import com.mockinterview.dto.auth.*;
import com.mockinterview.dto.common.ApiResponse;
import com.mockinterview.dto.common.MessageResponse;
import com.mockinterview.service.AuthService;
import com.mockinterview.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout user")
    public ResponseEntity<ApiResponse<MessageResponse>> logout(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        authService.logout(userPrincipal.getId());
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Logged out successfully").build()
                )
        );
    }

//    @GetMapping("/verify-email")
//    @Operation(summary = "Verify email address")
//    public ResponseEntity<ApiResponse<MessageResponse>> verifyEmail(
//            @Valid @RequestBody VerifyEmailRequest request
//    ) {
//        authService.verifyEmail(request);
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        MessageResponse.builder().message("Email verified successfully").build()
//                )
//        );
//    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email (API)")
    public ResponseEntity<ApiResponse<MessageResponse>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        authService.verifyEmail(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder()
                                .message("Email verified successfully")
                                .build()
                )
        );
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email via link")
    public ResponseEntity<ApiResponse<MessageResponse>> verifyEmailViaLink(
            @RequestParam String token
    ) {
        authService.verifyEmail(
                VerifyEmailRequest.builder()
                        .token(token)
                        .build()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder()
                                .message("Email verified successfully")
                                .build()
                )
        );
    }





    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email")
    public ResponseEntity<ApiResponse<MessageResponse>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        authService.resendVerificationEmail(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Verification email sent").build()
                )
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<ApiResponse<MessageResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Password reset link sent to email").build()
                )
        );
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<ApiResponse<MessageResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Password reset successful").build()
                )
        );
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change password (authenticated user)")
    public ResponseEntity<ApiResponse<MessageResponse>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Password changed successfully").build()
                )
        );
    }
}
