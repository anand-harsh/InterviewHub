package com.mockinterview.dto.skill;

import com.mockinterview.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSkillRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Skill.SkillCategory category;
}
