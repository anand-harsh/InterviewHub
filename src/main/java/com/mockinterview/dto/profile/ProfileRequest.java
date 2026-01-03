package com.mockinterview.dto.profile;

import com.mockinterview.entity.Profile;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    @NotBlank(message = "Current role is required")
    @Size(max = 100, message = "Current role must not exceed 100 characters")
    private String currentRole;

    @NotBlank(message = "Company is required")
    @Size(max = 100, message = "Company must not exceed 100 characters")
    private String company;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience must be realistic")
    private Integer yearsOfExperience;

    @NotNull(message = "Timezone is required")
    private Profile.Timezone timezone;

    @NotBlank(message = "Target role is required")
    @Size(max = 100, message = "Target role must not exceed 100 characters")
    private String targetRole;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private Profile.ProfileVisibility profileVisibility;
}