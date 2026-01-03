package com.mockinterview.dto.skill;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSkillsRequest {
    private Set<Long> skillIds;
    private Set<String> skillNames;

    public boolean isValid() {
        return (skillIds != null && !skillIds.isEmpty()) ||
                (skillNames != null && !skillNames.isEmpty());
    }
}