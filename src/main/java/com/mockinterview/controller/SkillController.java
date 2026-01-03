package com.mockinterview.controller;


import com.mockinterview.dto.common.ApiResponse;
import com.mockinterview.dto.common.MessageResponse;
import com.mockinterview.dto.common.PageResponse;
import com.mockinterview.dto.skill.AddSkillsRequest;
import com.mockinterview.dto.skill.SkillResponse;
import com.mockinterview.entity.Skill;
import com.mockinterview.service.SkillService;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skills management endpoints")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/skills")
    @Operation(summary = "Get all skills with pagination and search")
    public ResponseEntity<ApiResponse<PageResponse<SkillResponse>>> getAllSkills(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Skill.SkillCategory category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponse<SkillResponse> skills = skillService.getAllSkills(search, category, page, size);
        return ResponseEntity.ok(ApiResponse.success(skills));
    }

    @GetMapping("/skills/{id}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillById(@PathVariable Long id) {
        SkillResponse skill = skillService.getSkillById(id);
        return ResponseEntity.ok(ApiResponse.success(skill));
    }

    @GetMapping("/skills/categories")
    @Operation(summary = "Get all skill categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = skillService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/profile/skills")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add skills to profile")
    public ResponseEntity<ApiResponse<Set<SkillResponse>>> addSkillsToProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddSkillsRequest request
    ) {
        Set<SkillResponse> skills = skillService.addSkillsToProfile(userPrincipal.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skills added successfully", skills));
    }

    @DeleteMapping("/profile/skills/{skillId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove skill from profile")
    public ResponseEntity<ApiResponse<MessageResponse>> removeSkillFromProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long skillId
    ) {
        skillService.removeSkillFromProfile(userPrincipal.getId(), skillId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        MessageResponse.builder().message("Skill removed successfully").build()
                )
        );
    }

    @GetMapping("/profile/skills")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all skills from user's profile")
    public ResponseEntity<ApiResponse<Set<SkillResponse>>> getProfileSkills(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Set<SkillResponse> skills = skillService.getProfileSkills(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(skills));
    }
}
