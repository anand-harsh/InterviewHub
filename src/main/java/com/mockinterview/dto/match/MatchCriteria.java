package com.mockinterview.dto.match;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchCriteria {
    private String currentRole;
    private String targetRole;
    private String company;
    private String skills;
    private Integer minExperience;
    private Integer maxExperience;
    private String timezone;
    private Boolean availableOnly;
}