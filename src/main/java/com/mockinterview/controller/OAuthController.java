package com.mockinterview.controller;

import com.mockinterview.dto.common.ApiResponse;
import com.mockinterview.dto.common.MessageResponse;
import com.mockinterview.dto.oauth.OAuthStatusResponse;
import com.mockinterview.dto.oauth.TestConnectionResponse;
import com.mockinterview.security.UserPrincipal;
import com.mockinterview.service.GoogleCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "OAuth", description = "OAuth and Google Calendar integration endpoints")
public class OAuthController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/google/authorize")
    @Operation(summary = "Initiate Google OAuth flow")
    public void authorizeGoogleCalendar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletResponse response
    ) throws IOException {
        String authorizationUrl = googleCalendarService.getAuthorizationUrl(userPrincipal.getId());
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/google/callback")
    @Operation(summary = "Handle Google OAuth callback")
    public void handleGoogleCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response
    ) throws IOException {
        try {
            Long userId = googleCalendarService.extractUserIdFromState(state);
            googleCalendarService.handleCallback(userId, code);

            // Redirect to frontend with success
            response.sendRedirect(googleCalendarService.getFrontendSuccessUrl());
        } catch (Exception e) {
            // Redirect to frontend with error
            response.sendRedirect(googleCalendarService.getFrontendErrorUrl(e.getMessage()));
        }
    }

    @PostMapping("/google/disconnect")
    @Operation(summary = "Disconnect Google Calendar")
    public ResponseEntity<ApiResponse<MessageResponse>> disconnectGoogleCalendar(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        googleCalendarService.disconnect(userPrincipal.getId());
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder()
                                .message("Google Calendar disconnected successfully")
                                .build()
                )
        );
    }

    @GetMapping("/status")
    @Operation(summary = "Get OAuth connection status")
    public ResponseEntity<ApiResponse<OAuthStatusResponse>> getOAuthStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        OAuthStatusResponse status = googleCalendarService.getOAuthStatus(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @PostMapping("/test-connection")
    @Operation(summary = "Test OAuth connection")
    public ResponseEntity<ApiResponse<TestConnectionResponse>> testConnection(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TestConnectionResponse result = googleCalendarService.testConnection(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}