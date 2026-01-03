package com.mockinterview.dto.match;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatchDetailResponse {
    private Long userId;
    private ProfileInfo profile;
    private Integer matchScore;
    private MatchDetails matchDetails;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileInfo {
        private Long id;
        private String name;
        private String currentRole;
        private String company;
        private Integer yearsOfExperience;
        private String timezone;
        private String targetRole;
        private String bio;
        private Boolean availableForInterview;
        private Set<String> skills;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchDetails {
        // Role matching
        private Boolean roleMatch;
        private String roleMatchType; // "INTERVIEWER", "PEER", "REVERSE"
        private String roleMatchDescription;

        // Skills matching
        private Set<String> skillsOverlap;
        private Integer skillsOverlapCount;
        private Integer totalSkillsCount;
        private Double skillsOverlapPercentage;

        // Experience matching
        private Boolean experienceSimilar;
        private Integer experienceDifference;
        private String experienceMatchLevel; // "EXACT", "SIMILAR", "DIFFERENT"

        // Timezone compatibility
        private Boolean timezoneCompatible;
        private String timezoneCompatibilityLevel; // "SAME", "COMPATIBLE", "DIFFERENT"

        // Overall analysis
        private List<String> strengths; // Why this is a good match
        private List<String> considerations; // Things to consider
        private String recommendation; // Overall recommendation
    }
}
