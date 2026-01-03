package com.mockinterview.service;

import com.mockinterview.dto.common.PageResponse;
import com.mockinterview.dto.match.MatchResponse;
import com.mockinterview.dto.match.UserMatchDetailResponse;
import com.mockinterview.entity.Profile;
import com.mockinterview.entity.Skill;
import com.mockinterview.exception.ResourceNotFoundException;
import com.mockinterview.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> getSmartSuggestions(
            Long userId, Integer page, Integer size, Integer minScore
    ) {
        Profile currentProfile = getProfileWithSkills(userId);
        Pageable pageable = PageRequest.of(page, size);

        // Get all potential matches (exclude self, include only available users)
        Page<Profile> allProfiles = profileRepository.findAllExcludingUserAndAvailable(
                userId, currentProfile.getAvailableForInterview(), pageable
        );

        // Calculate match scores for each profile
        List<MatchResponse> matches = allProfiles.getContent().stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .filter(match -> minScore == null || match.getMatchScore() >= minScore)
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        return PageResponse.<MatchResponse>builder()
                .content(matches)
                .page(allProfiles.getNumber())
                .size(allProfiles.getSize())
                .totalElements(allProfiles.getTotalElements())
                .totalPages(allProfiles.getTotalPages())
                .first(allProfiles.isFirst())
                .last(allProfiles.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> advancedSearch(
            Long userId, String currentJobRole, String targetRole, String company,
            String skills, Integer minExp, Integer maxExp, String timezone,
            Boolean availableOnly, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);
        Pageable pageable = PageRequest.of(page, size);

        // Parse skills if provided
        Set<String> searchSkills = skills != null && !skills.isBlank()
                ? Arrays.stream(skills.split(","))
                .map(String::trim)
                .collect(Collectors.toSet())
                : new HashSet<>();

        // Get all profiles except current user
        List<Profile> allProfiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(p -> !availableOnly || p.getAvailableForInterview())
                .filter(p -> currentJobRole == null ||
                        p.getCurrentJobRole().toLowerCase().contains(currentJobRole.toLowerCase()))
                .filter(p -> targetRole == null ||
                        p.getTargetRole().toLowerCase().contains(targetRole.toLowerCase()))
                .filter(p -> company == null ||
                        p.getCompany().toLowerCase().contains(company.toLowerCase()))
                .filter(p -> minExp == null || p.getYearsOfExperience() >= minExp)
                .filter(p -> maxExp == null || p.getYearsOfExperience() <= maxExp)
                .filter(p -> timezone == null ||
                        p.getTimezone().name().equalsIgnoreCase(timezone))
                .filter(p -> searchSkills.isEmpty() || hasAnySkill(p, searchSkills))
                .collect(Collectors.toList());

        // Calculate matches
        List<MatchResponse> matches = allProfiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        // Paginate results
        int start = page * size;
        int end = Math.min(start + size, matches.size());
        List<MatchResponse> paginatedMatches = start < matches.size()
                ? matches.subList(start, end)
                : new ArrayList<>();

        return PageResponse.<MatchResponse>builder()
                .content(paginatedMatches)
                .page(page)
                .size(size)
                .totalElements((long) matches.size())
                .totalPages((int) Math.ceil((double) matches.size() / size))
                .first(page == 0)
                .last(end >= matches.size())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> findBySkillOverlap(
            Long userId, Integer minMatchingSkills, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);

        if (currentProfile.getSkills().isEmpty()) {
            return emptyPageResponse(page, size);
        }

        Set<String> currentSkills = currentProfile.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        List<Profile> profiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(Profile::getAvailableForInterview)
                .filter(p -> countMatchingSkills(p, currentSkills) >= minMatchingSkills)
                .collect(Collectors.toList());

        List<MatchResponse> matches = profiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(
                        b.getMatchingSkills().size(),
                        a.getMatchingSkills().size()
                ))
                .collect(Collectors.toList());

        return paginateResults(matches, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> findInterviewersForTargetRole(
            Long userId, Integer minExperience, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);

        List<Profile> profiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(Profile::getAvailableForInterview)
                .filter(p -> p.getCurrentJobRole().equalsIgnoreCase(currentProfile.getTargetRole()))
                .filter(p -> p.getYearsOfExperience() >= minExperience)
                .collect(Collectors.toList());

        List<MatchResponse> matches = profiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        return paginateResults(matches, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> findReverseMatches(
            Long userId, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);

        List<Profile> profiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(Profile::getAvailableForInterview)
                .filter(p -> p.getTargetRole().equalsIgnoreCase(currentProfile.getCurrentJobRole()))
                .collect(Collectors.toList());

        List<MatchResponse> matches = profiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        return paginateResults(matches, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> findInSameTimezone(
            Long userId, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);

        List<Profile> profiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(Profile::getAvailableForInterview)
                .filter(p -> p.getTimezone() == currentProfile.getTimezone())
                .collect(Collectors.toList());

        List<MatchResponse> matches = profiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        return paginateResults(matches, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<MatchResponse> findSimilarExperience(
            Long userId, Integer variance, Integer page, Integer size
    ) {
        Profile currentProfile = getProfileWithSkills(userId);

        int minExp = Math.max(0, currentProfile.getYearsOfExperience() - variance);
        int maxExp = currentProfile.getYearsOfExperience() + variance;

        List<Profile> profiles = profileRepository.findAllWithSkills().stream()
                .filter(p -> !p.getUser().getId().equals(userId))
                .filter(Profile::getAvailableForInterview)
                .filter(p -> p.getYearsOfExperience() >= minExp &&
                        p.getYearsOfExperience() <= maxExp)
                .collect(Collectors.toList());

        List<MatchResponse> matches = profiles.stream()
                .map(profile -> calculateMatch(currentProfile, profile))
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        return paginateResults(matches, page, size);
    }

    @Transactional(readOnly = true)
    public UserMatchDetailResponse getMatchDetails(Long currentUserId, Long targetUserId) {
        Profile currentProfile = getProfileWithSkills(currentUserId);
        Profile targetProfile = getProfileWithSkills(targetUserId);

        MatchResponse match = calculateMatch(currentProfile, targetProfile);

        return buildDetailedMatchResponse(currentProfile, targetProfile, match);
    }

    // ==================== HELPER METHODS ====================

    private MatchResponse calculateMatch(Profile current, Profile target) {
        // Calculate individual scores
        int skillScore = calculateSkillMatchScore(current, target);
        int roleScore = calculateRoleMatchScore(current, target);
        int experienceScore = calculateExperienceMatchScore(current, target);
        int timezoneScore = calculateTimezoneMatchScore(current, target);

        // Weighted overall score
        int overallScore = (int) (
                skillScore * 0.40 +      // 40% weight on skills
                        roleScore * 0.35 +       // 35% weight on roles
                        experienceScore * 0.15 + // 15% weight on experience
                        timezoneScore * 0.10     // 10% weight on timezone
        );

        // Determine match reasons
        List<String> reasons = new ArrayList<>();
        String primaryReason = "";

        if (target.getCurrentJobRole().equalsIgnoreCase(current.getTargetRole())) {
            reasons.add("Can interview you for " + current.getTargetRole());
            primaryReason = "Perfect interviewer for your target role";
        } else if (target.getTargetRole().equalsIgnoreCase(current.getCurrentJobRole())) {
            reasons.add("Preparing for your current role");
            primaryReason = "You can help them prepare";
        }

        Set<String> matchingSkills = getMatchingSkills(current, target);
        if (!matchingSkills.isEmpty()) {
            reasons.add(matchingSkills.size() + " shared skills");
            if (primaryReason.isEmpty()) {
                primaryReason = matchingSkills.size() + " matching skills";
            }
        }

        if (target.getTimezone() == current.getTimezone()) {
            reasons.add("Same timezone (" + target.getTimezone() + ")");
        }

        int expDiff = Math.abs(target.getYearsOfExperience() - current.getYearsOfExperience());
        if (expDiff <= 2) {
            reasons.add("Similar experience level");
        }

        if (primaryReason.isEmpty()) {
            primaryReason = "Available for mock interviews";
        }

        // Build response
        return MatchResponse.builder()
                .userId(target.getUser().getId())
                .profileId(target.getId())
                .name(target.getUser().getEmail())
                .currentJobRole(target.getCurrentJobRole())
                .company(target.getCompany())
                .yearsOfExperience(target.getYearsOfExperience())
                .timezone(target.getTimezone())
                .targetRole(target.getTargetRole())
                .bio(target.getBio())
                .availableForInterview(target.getAvailableForInterview())
                .matchScore(overallScore)
                .matchReason(primaryReason)
                .matchReasons(reasons)
                .matchBreakdown(MatchResponse.MatchBreakdown.builder()
                        .skillMatchScore(skillScore)
                        .roleMatchScore(roleScore)
                        .experienceMatchScore(experienceScore)
                        .timezoneMatchScore(timezoneScore)
                        .overallScore(overallScore)
                        .build())
                .skills(target.getSkills().stream()
                        .map(skill -> MatchResponse.SkillDTO.builder()
                                .id(skill.getId())
                                .name(skill.getName())
                                .category(skill.getCategory().name())
                                .build())
                        .collect(Collectors.toSet()))
                .matchingSkills(matchingSkills)
                .build();
    }

    private int calculateSkillMatchScore(Profile current, Profile target) {
        if (current.getSkills().isEmpty() && target.getSkills().isEmpty()) {
            return 50; // Neutral score
        }
        if (current.getSkills().isEmpty() || target.getSkills().isEmpty()) {
            return 20; // Low score if one has no skills
        }

        Set<String> currentSkills = current.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> targetSkills = target.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(currentSkills);
        intersection.retainAll(targetSkills);

        Set<String> union = new HashSet<>(currentSkills);
        union.addAll(targetSkills);

        // Jaccard similarity
        double similarity = (double) intersection.size() / union.size();
        return (int) (similarity * 100);
    }

    private int calculateRoleMatchScore(Profile current, Profile target) {
        int score = 0;

        // Perfect match: target can interview for current's target role
        if (target.getCurrentJobRole().equalsIgnoreCase(current.getTargetRole())) {
            score += 90;
        }
        // Good match: current can interview for target's target role
        else if (target.getTargetRole().equalsIgnoreCase(current.getCurrentJobRole())) {
            score += 85;
        }
        // Same target role (peer practice)
        else if (target.getTargetRole().equalsIgnoreCase(current.getTargetRole())) {
            score += 70;
        }
        // Same current role (peer practice)
        else if (target.getCurrentJobRole().equalsIgnoreCase(current.getCurrentJobRole())) {
            score += 60;
        }
        // Partial match
        else if (target.getCurrentJobRole().toLowerCase().contains(current.getTargetRole().toLowerCase()) ||
                current.getTargetRole().toLowerCase().contains(target.getCurrentJobRole().toLowerCase())) {
            score += 50;
        }
        else {
            score = 30; // Base score
        }

        return Math.min(score, 100);
    }

    private int calculateExperienceMatchScore(Profile current, Profile target) {
        int diff = Math.abs(current.getYearsOfExperience() - target.getYearsOfExperience());

        if (diff == 0) return 100;
        if (diff <= 1) return 90;
        if (diff <= 2) return 80;
        if (diff <= 3) return 70;
        if (diff <= 5) return 60;
        return Math.max(30, 100 - (diff * 10));
    }

    private int calculateTimezoneMatchScore(Profile current, Profile target) {
        if (current.getTimezone() == target.getTimezone()) {
            return 100;
        }

        // Compatible timezones (e.g., EST and PST can still work)
        return 60;
    }

    private Set<String> getMatchingSkills(Profile current, Profile target) {
        Set<String> currentSkills = current.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> targetSkills = target.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        currentSkills.retainAll(targetSkills);
        return currentSkills;
    }

    private boolean hasAnySkill(Profile profile, Set<String> skillNames) {
        return profile.getSkills().stream()
                .anyMatch(skill -> skillNames.contains(skill.getName()));
    }

    private int countMatchingSkills(Profile profile, Set<String> skillNames) {
        return (int) profile.getSkills().stream()
                .filter(skill -> skillNames.contains(skill.getName()))
                .count();
    }

    private Profile getProfileWithSkills(Long userId) {
        return profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));
    }

    private UserMatchDetailResponse buildDetailedMatchResponse(
            Profile current, Profile target, MatchResponse match
    ) {
        Set<String> matchingSkills = match.getMatchingSkills();
        int totalSkills = Math.max(
                current.getSkills().size() + target.getSkills().size() - matchingSkills.size(),
                1
        );

        // Determine role match type
        String roleMatchType;
        String roleMatchDescription;
        boolean roleMatch = false;

        if (target.getCurrentJobRole().equalsIgnoreCase(current.getTargetRole())) {
            roleMatch = true;
            roleMatchType = "INTERVIEWER";
            roleMatchDescription = target.getUser().getEmail() + " can interview you for " +
                    current.getTargetRole();
        } else if (target.getTargetRole().equalsIgnoreCase(current.getCurrentJobRole())) {
            roleMatch = true;
            roleMatchType = "REVERSE";
            roleMatchDescription = "You can interview " + target.getUser().getEmail() +
                    " for " + target.getTargetRole();
        } else if (target.getTargetRole().equalsIgnoreCase(current.getTargetRole())) {
            roleMatch = true;
            roleMatchType = "PEER";
            roleMatchDescription = "Both preparing for " + current.getTargetRole();
        } else {
            roleMatchType = "NONE";
            roleMatchDescription = "No direct role match";
        }

        // Experience analysis
        int expDiff = Math.abs(target.getYearsOfExperience() - current.getYearsOfExperience());
        String experienceMatchLevel;
        boolean experienceSimilar;

        if (expDiff == 0) {
            experienceMatchLevel = "EXACT";
            experienceSimilar = true;
        } else if (expDiff <= 2) {
            experienceMatchLevel = "SIMILAR";
            experienceSimilar = true;
        } else {
            experienceMatchLevel = "DIFFERENT";
            experienceSimilar = false;
        }

        // Timezone analysis
        boolean timezoneCompatible = target.getTimezone() == current.getTimezone();
        String timezoneCompatibilityLevel = timezoneCompatible ? "SAME" : "DIFFERENT";

        // Build strengths and considerations
        List<String> strengths = new ArrayList<>();
        List<String> considerations = new ArrayList<>();

        if (roleMatch) {
            strengths.add("Excellent role match - " + roleMatchDescription);
        }
        if (!matchingSkills.isEmpty()) {
            strengths.add(matchingSkills.size() + " shared skills: " +
                    String.join(", ", matchingSkills));
        }
        if (timezoneCompatible) {
            strengths.add("Same timezone for easy scheduling");
        }
        if (experienceSimilar) {
            strengths.add("Similar experience level");
        }

        if (!timezoneCompatible) {
            considerations.add("Different timezones - may require flexible scheduling");
        }
        if (expDiff > 3) {
            considerations.add("Significant experience gap (" + expDiff + " years)");
        }
        if (matchingSkills.size() < 3) {
            considerations.add("Limited skill overlap");
        }

        // Overall recommendation
        String recommendation;
        if (match.getMatchScore() >= 80) {
            recommendation = "Highly recommended match! Great compatibility across multiple factors.";
        } else if (match.getMatchScore() >= 60) {
            recommendation = "Good match with solid compatibility. Consider reaching out!";
        } else if (match.getMatchScore() >= 40) {
            recommendation = "Moderate match. Could work with some adjustments.";
        } else {
            recommendation = "Limited compatibility. Consider other matches first.";
        }

        return UserMatchDetailResponse.builder()
                .userId(target.getUser().getId())
                .profile(UserMatchDetailResponse.ProfileInfo.builder()
                        .id(target.getId())
                        .name(target.getUser().getEmail())
                        .currentJobRole(target.getCurrentJobRole())
                        .company(target.getCompany())
                        .yearsOfExperience(target.getYearsOfExperience())
                        .timezone(target.getTimezone().name())
                        .targetRole(target.getTargetRole())
                        .bio(target.getBio())
                        .availableForInterview(target.getAvailableForInterview())
                        .skills(target.getSkills().stream()
                                .map(Skill::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .matchScore(match.getMatchScore())
                .matchDetails(UserMatchDetailResponse.MatchDetails.builder()
                        .roleMatch(roleMatch)
                        .roleMatchType(roleMatchType)
                        .roleMatchDescription(roleMatchDescription)
                        .skillsOverlap(matchingSkills)
                        .skillsOverlapCount(matchingSkills.size())
                        .totalSkillsCount(totalSkills)
                        .skillsOverlapPercentage((double) matchingSkills.size() / totalSkills * 100)
                        .experienceSimilar(experienceSimilar)
                        .experienceDifference(expDiff)
                        .experienceMatchLevel(experienceMatchLevel)
                        .timezoneCompatible(timezoneCompatible)
                        .timezoneCompatibilityLevel(timezoneCompatibilityLevel)
                        .strengths(strengths)
                        .considerations(considerations)
                        .recommendation(recommendation)
                        .build())
                .build();
    }

    private PageResponse<MatchResponse> paginateResults(
            List<MatchResponse> matches, Integer page, Integer size
    ) {
        int start = page * size;
        int end = Math.min(start + size, matches.size());
        List<MatchResponse> paginatedMatches = start < matches.size()
                ? matches.subList(start, end)
                : new ArrayList<>();

        return PageResponse.<MatchResponse>builder()
                .content(paginatedMatches)
                .page(page)
                .size(size)
                .totalElements((long) matches.size())
                .totalPages((int) Math.ceil((double) matches.size() / size))
                .first(page == 0)
                .last(end >= matches.size())
                .build();
    }

    private PageResponse<MatchResponse> emptyPageResponse(Integer page, Integer size) {
        return PageResponse.<MatchResponse>builder()
                .content(new ArrayList<>())
                .page(page)
                .size(size)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();
    }
}