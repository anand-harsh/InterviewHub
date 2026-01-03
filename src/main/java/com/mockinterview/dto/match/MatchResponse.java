// MatchResponse.java
package com.mockinterview.dto.match;

import com.mockinterview.entity.Profile;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {
    private Long userId;
    private Long profileId;
    private String name; // Will be email for now, can add real name later
    private String currentJobRole;
    private String company;
    private Integer yearsOfExperience;
    private Profile.Timezone timezone;
    private String targetRole;
    private String bio;
    private Boolean availableForInterview;

    // Matching information
    private Integer matchScore; // 0-100
    private String matchReason; // Primary reason for match
    private List<String> matchReasons; // All reasons

    // Detailed match breakdown
    private MatchBreakdown matchBreakdown;

    // Skills
    private Set<SkillDTO> skills;
    private Set<String> matchingSkills; // Skills that match with current user

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDTO {
        private Long id;
        private String name;
        private String category;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchBreakdown {
        private Integer skillMatchScore; // 0-100
        private Integer roleMatchScore; // 0-100
        private Integer experienceMatchScore; // 0-100
        private Integer timezoneMatchScore; // 0-100
        private Integer overallScore; // 0-100
    }
}
