package com.mockinterview.dto.profile;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileCompletionResponse {
    private Integer score;
    private Set<String> missingFields;
    private Set<String> suggestions;
}