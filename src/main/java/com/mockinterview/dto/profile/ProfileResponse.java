package com.mockinterview.dto.profile;

import com.mockinterview.entity.Profile;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private Long userId;
    private String currentRole;
    private String company;
    private Integer yearsOfExperience;
    private Profile.Timezone timezone;
    private String targetRole;
    private String bio;
    private Boolean availableForInterview;
    private Profile.ProfileVisibility profileVisibility;
    private Integer profileCompletionScore;
    private Set<DeactivateAccountRequest.SkillDTO> skills;
    private Instant createdAt;
    private Instant updatedAt;
}