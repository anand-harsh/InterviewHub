package com.mockinterview.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills", indexes = {
        @Index(name = "idx_skill_name", columnList = "name"),
        @Index(name = "idx_skill_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "skills")
    @Builder.Default
    private Set<Profile> profiles = new HashSet<>();

    public enum SkillCategory {
        PROGRAMMING,
        FRAMEWORKS,
        DATABASES,
        CLOUD,
        DEVOPS,
        SOFT_SKILLS,
        TOOLS,
        LANGUAGES,
        TESTING,
        SECURITY,
        MOBILE,
        WEB,
        DATA_SCIENCE,
        AI_ML,
        BLOCKCHAIN,
        OTHER
    }
}