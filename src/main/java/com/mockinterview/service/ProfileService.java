package com.mockinterview.service;

import com.mockinterview.dto.profile.*;
import com.mockinterview.entity.Profile;
import com.mockinterview.entity.User;
import com.mockinterview.exception.BadRequestException;
import com.mockinterview.exception.DuplicateResourceException;
import com.mockinterview.exception.ResourceNotFoundException;
import com.mockinterview.repository.ProfileRepository;
import com.mockinterview.repository.UserRepository;
import com.mockinterview.util.ProfileCompletionCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileCompletionCalculator completionCalculator;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        Profile profile = profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        return mapToResponse(profile);
    }

    @Transactional
    public ProfileResponse createProfile(Long userId, ProfileRequest request) {
        // Check if profile already exists
        if (profileRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("Profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Profile profile = Profile.builder()
                .user(user)
                .currentRole(request.getCurrentRole())
                .company(request.getCompany())
                .yearsOfExperience(request.getYearsOfExperience())
                .timezone(request.getTimezone())
                .targetRole(request.getTargetRole())
                .bio(request.getBio())
                .availableForInterview(true)
                .profileVisibility(request.getProfileVisibility() != null ?
                        request.getProfileVisibility() : Profile.ProfileVisibility.PUBLIC)
                .build();

        // Calculate completion score
        profile.setProfileCompletionScore(completionCalculator.calculate(profile));

        profile = profileRepository.save(profile);

        log.info("Profile created for user: {}", userId);

        return mapToResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        // Update fields
        profile.setCurrentRole(request.getCurrentRole());
        profile.setCompany(request.getCompany());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setTimezone(request.getTimezone());
        profile.setTargetRole(request.getTargetRole());
        profile.setBio(request.getBio());

        if (request.getProfileVisibility() != null) {
            profile.setProfileVisibility(request.getProfileVisibility());
        }

        // Recalculate completion score
        profile.setProfileCompletionScore(completionCalculator.calculate(profile));

        profile = profileRepository.save(profile);

        log.info("Profile updated for user: {}", userId);

        return mapToResponse(profile);
    }

    @Transactional
    public void updateAvailability(Long userId, UpdateAvailabilityRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        profile.setAvailableForInterview(request.getAvailableForInterview());
        profileRepository.save(profile);

        log.info("Availability updated for user: {} to {}", userId, request.getAvailableForInterview());
    }

    @Transactional
    public void deactivateAccount(Long userId, DeactivateAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setStatus(User.UserStatus.DEACTIVATED);
        userRepository.save(user);

        // Also update profile availability
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setAvailableForInterview(false);
            profileRepository.save(profile);
        });

        log.info("Account deactivated for user: {}. Reason: {}", userId, request.getReason());
    }

    @Transactional
    public void reactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getStatus() != User.UserStatus.DEACTIVATED) {
            throw new BadRequestException("Account is not deactivated");
        }

        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("Account reactivated for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public ProfileCompletionResponse getCompletionScore(Long userId) {
        Profile profile = profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        return completionCalculator.getCompletionDetails(profile);
    }

    private ProfileResponse mapToResponse(Profile profile) {
        Set<DeactivateAccountRequest.SkillDTO> skillDTOs = profile.getSkills().stream()
                .map(skill -> DeactivateAccountRequest.SkillDTO.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .category(skill.getCategory().name())
                        .build())
                .collect(Collectors.toSet());

        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .currentRole(profile.getCurrentRole())
                .company(profile.getCompany())
                .yearsOfExperience(profile.getYearsOfExperience())
                .timezone(profile.getTimezone())
                .targetRole(profile.getTargetRole())
                .bio(profile.getBio())
                .availableForInterview(profile.getAvailableForInterview())
                .profileVisibility(profile.getProfileVisibility())
                .profileCompletionScore(profile.getProfileCompletionScore())
                .skills(skillDTOs)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}