package com.mockinterview.controller;

import com.mockinterview.dto.common.ApiResponse;
import com.mockinterview.dto.common.PageResponse;
import com.mockinterview.dto.match.MatchResponse;
import com.mockinterview.dto.match.UserMatchDetailResponse;
import com.mockinterview.security.UserPrincipal;
import com.mockinterview.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Matching & Discovery", description = "Find and match with interview partners")
public class MatchController {

    private final MatchingService matchingService;

    @GetMapping("/suggestions")
    @Operation(
            summary = "Get smart match suggestions",
            description = "Returns users that best match your profile based on skills, roles, experience, and timezone. " +
                    "Includes match score and reasons for the match."
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> getMatchSuggestions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "Minimum match score (0-100)")
            @RequestParam(required = false) Integer minScore
    ) {
        PageResponse<MatchResponse> matches = matchingService.getSmartSuggestions(
                userPrincipal.getId(), page, size, minScore
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Advanced search for interview partners",
            description = "Search users with specific filters: role, skills, experience, timezone, availability"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> searchMatches(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Current role to search for")
            @RequestParam(required = false) String currentRole,
            @Parameter(description = "Target role to search for")
            @RequestParam(required = false) String targetRole,
            @Parameter(description = "Company name")
            @RequestParam(required = false) String company,
            @Parameter(description = "Comma-separated skill names")
            @RequestParam(required = false) String skills,
            @Parameter(description = "Minimum years of experience")
            @RequestParam(required = false) Integer minExperience,
            @Parameter(description = "Maximum years of experience")
            @RequestParam(required = false) Integer maxExperience,
            @Parameter(description = "Timezone")
            @RequestParam(required = false) String timezone,
            @Parameter(description = "Only show available users")
            @RequestParam(required = false, defaultValue = "true") Boolean availableOnly,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> results = matchingService.advancedSearch(
                userPrincipal.getId(), currentRole, targetRole, company, skills,
                minExperience, maxExperience, timezone, availableOnly, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/by-skills")
    @Operation(
            summary = "Find matches based on skill overlap",
            description = "Returns users with similar skills, sorted by number of matching skills"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> findBySkills(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Minimum number of matching skills")
            @RequestParam(required = false, defaultValue = "1") Integer minMatchingSkills,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> matches = matchingService.findBySkillOverlap(
                userPrincipal.getId(), minMatchingSkills, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/by-target-role")
    @Operation(
            summary = "Find interviewers for your target role",
            description = "Returns users whose current role matches your target role (potential interviewers)"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> findInterviewersForTargetRole(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Minimum years of experience in that role")
            @RequestParam(required = false, defaultValue = "2") Integer minExperience,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> matches = matchingService.findInterviewersForTargetRole(
                userPrincipal.getId(), minExperience, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/reverse")
    @Operation(
            summary = "Find people preparing for your current role",
            description = "Returns users whose target role matches your current role (you can interview them)"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> findReverseMatches(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> matches = matchingService.findReverseMatches(
                userPrincipal.getId(), page, size
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get detailed match info for a specific user",
            description = "Returns detailed matching analysis between you and another user"
    )
    public ResponseEntity<ApiResponse<UserMatchDetailResponse>> getMatchDetails(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "User ID to get match details for")
            @PathVariable Long userId
    ) {
        UserMatchDetailResponse details = matchingService.getMatchDetails(
                userPrincipal.getId(), userId
        );
        return ResponseEntity.ok(ApiResponse.success(details));
    }

    @GetMapping("/same-timezone")
    @Operation(
            summary = "Find users in the same timezone",
            description = "Returns users in your timezone for easier scheduling"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> findSameTimezone(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> matches = matchingService.findInSameTimezone(
                userPrincipal.getId(), page, size
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }

    @GetMapping("/similar-experience")
    @Operation(
            summary = "Find users with similar experience level",
            description = "Returns users with similar years of experience (±2 years)"
    )
    public ResponseEntity<ApiResponse<PageResponse<MatchResponse>>> findSimilarExperience(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Experience variance in years")
            @RequestParam(required = false, defaultValue = "2") Integer variance,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<MatchResponse> matches = matchingService.findSimilarExperience(
                userPrincipal.getId(), variance, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(matches));
    }
}