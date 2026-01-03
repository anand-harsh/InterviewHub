package com.mockinterview.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles", indexes = {
        @Index(name = "idx_target_role", columnList = "target_role"),
        @Index(name = "idx_current_role", columnList = "current_role"),
        @Index(name = "idx_available", columnList = "available_for_interview")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_role", nullable = false, length = 100)
    private String currentRole;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Timezone timezone;

    @Column(name = "target_role", nullable = false, length = 100)
    private String targetRole;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "available_for_interview", nullable = false)
    private Boolean availableForInterview = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false, length = 20)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(name = "profile_completion_score")
    private Integer profileCompletionScore = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "profile_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    public enum Timezone {
        PST, MST, CST, EST, GMT, CET, IST, JST, AEST
    }

    public enum ProfileVisibility {
        PUBLIC,
        PRIVATE,
        CONNECTIONS_ONLY
    }

    public void addSkill(Skill skill) {
        this.skills.add(skill);
        skill.getProfiles().add(this);
    }

    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
        skill.getProfiles().remove(this);
    }
}