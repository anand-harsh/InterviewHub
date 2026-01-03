package com.mockinterview.controller;


import com.mockinterview.dto.common.ApiResponse;
import com.mockinterview.dto.common.MessageResponse;
import com.mockinterview.dto.profile.*;
import com.mockinterview.service.ProfileService;
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
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile", description = "User profile management endpoints")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Get current user's profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ProfileResponse profile = profileService.getProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping
    @Operation(summary = "Create profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProfileRequest request
    ) {
        ProfileResponse profile = profileService.createProfile(userPrincipal.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created successfully", profile));
    }

    @PutMapping
    @Operation(summary = "Update profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProfileRequest request
    ) {
        ProfileResponse profile = profileService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PatchMapping("/availability")
    @Operation(summary = "Update availability status")
    public ResponseEntity<ApiResponse<MessageResponse>> updateAvailability(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateAvailabilityRequest request
    ) {
        profileService.updateAvailability(userPrincipal.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder()
                                .message("Availability updated to " + request.getAvailableForInterview())
                                .build()
                )
        );
    }

    @PatchMapping("/deactivate")
    @Operation(summary = "Deactivate account")
    public ResponseEntity<ApiResponse<MessageResponse>> deactivateAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody(required = false) DeactivateAccountRequest request
    ) {
        profileService.deactivateAccount(
                userPrincipal.getId(),
                request != null ? request : new DeactivateAccountRequest()
        );
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Account deactivated successfully").build()
                )
        );
    }

    @PatchMapping("/reactivate")
    @Operation(summary = "Reactivate account")
    public ResponseEntity<ApiResponse<MessageResponse>> reactivateAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        profileService.reactivateAccount(userPrincipal.getId());
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Account reactivated successfully").build()
                )
        );
    }

    @GetMapping("/completion-score")
    @Operation(summary = "Get profile completion score")
    public ResponseEntity<ApiResponse<ProfileCompletionResponse>> getCompletionScore(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ProfileCompletionResponse score = profileService.getCompletionScore(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(score));
    }
}