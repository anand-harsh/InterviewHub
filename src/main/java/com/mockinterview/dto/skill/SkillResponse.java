package com.mockinterview.dto.skill;

import com.mockinterview.entity.Skill;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {
    private Long id;
    private String name;
    private Skill.SkillCategory category;
    private String description;
    private Instant createdAt;

}