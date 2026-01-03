package com.mockinterview.dto.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeactivateAccountRequest {
    private String reason;

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
}