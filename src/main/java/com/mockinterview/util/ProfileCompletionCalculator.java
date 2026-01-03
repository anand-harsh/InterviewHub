package com.mockinterview.util;

import com.mockinterview.dto.profile.ProfileCompletionResponse;
import com.mockinterview.entity.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ProfileCompletionCalculator {

    public Integer calculate(Profile profile) {
        int totalFields = 7;
        int completedFields = 0;

        if (profile.getCurrentJobRole() != null && !profile.getCurrentJobRole().isEmpty()) {
            completedFields++;
        }
        if (profile.getCompany() != null && !profile.getCompany().isEmpty()) {
            completedFields++;
        }
        if (profile.getYearsOfExperience() != null) {
            completedFields++;
        }
        if (profile.getTimezone() != null) {
            completedFields++;
        }
        if (profile.getTargetRole() != null && !profile.getTargetRole().isEmpty()) {
            completedFields++;
        }
        if (profile.getBio() != null && !profile.getBio().isEmpty()) {
            completedFields++;
        }
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            completedFields++;
        }

        return (int) ((completedFields / (double) totalFields) * 100);
    }

    public ProfileCompletionResponse getCompletionDetails(Profile profile) {
        Set<String> missingFields = new HashSet<>();
        Set<String> suggestions = new HashSet<>();

        if (profile.getBio() == null || profile.getBio().isEmpty()) {
            missingFields.add("bio");
            suggestions.add("Add a bio to help others understand your background");
        }

        if (profile.getSkills() == null || profile.getSkills().isEmpty()) {
            missingFields.add("skills");
            suggestions.add("Add at least 3-5 skills to increase your match potential");
        } else if (profile.getSkills().size() < 3) {
            suggestions.add("Add more skills (currently " + profile.getSkills().size() + ", recommended: 5+)");
        }

        Integer score = calculate(profile);

        if (score < 100) {
            suggestions.add("Complete all profile fields to get better match suggestions");
        }

        return ProfileCompletionResponse.builder()
                .score(score)
                .missingFields(missingFields)
                .suggestions(suggestions)
                .build();
    }
}