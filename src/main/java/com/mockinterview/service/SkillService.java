package com.mockinterview.service;

import com.mockinterview.dto.common.PageResponse;
import com.mockinterview.dto.skill.AddSkillsRequest;
import com.mockinterview.dto.skill.SkillResponse;
import com.mockinterview.entity.Profile;
import com.mockinterview.entity.Skill;
import com.mockinterview.exception.BadRequestException;
import com.mockinterview.exception.ResourceNotFoundException;
import com.mockinterview.repository.ProfileRepository;
import com.mockinterview.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    // -------------------- READ OPERATIONS --------------------

    @Transactional(readOnly = true)
    public PageResponse<SkillResponse> getAllSkills(
            String search,
            Skill.SkillCategory category,
            Integer page,
            Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Skill> skillPage;

        if (category != null && search != null && !search.isBlank()) {
            skillPage = skillRepository.searchByCategoryAndName(category, search, pageable);
        } else if (search != null && !search.isBlank()) {
            skillPage = skillRepository.searchByName(search, pageable);
        } else if (category != null) {
            // Manual pagination for category-only filter
            List<Skill> allByCategory = skillRepository.findByCategory(category);

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), allByCategory.size());

            List<Skill> pagedList =
                    start > allByCategory.size() ? List.of() : allByCategory.subList(start, end);

            skillPage = new PageImpl<>(pagedList, pageable, allByCategory.size());
        } else {
            skillPage = skillRepository.findAll(pageable);
        }

        List<SkillResponse> content = skillPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<SkillResponse>builder()
                .content(content)
                .page(skillPage.getNumber())
                .size(skillPage.getSize())
                .totalElements(skillPage.getTotalElements())
                .totalPages(skillPage.getTotalPages())
                .first(skillPage.isFirst())
                .last(skillPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        return mapToResponse(skill);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return List.of(Skill.SkillCategory.values())
                .stream()
                .map(Enum::name)
                .toList();
    }

    // -------------------- WRITE OPERATIONS --------------------

    @Transactional
    public Set<SkillResponse> addSkillsToProfile(Long userId, AddSkillsRequest request) {
        if (!request.isValid()) {
            throw new BadRequestException("Either skillIds or skillNames must be provided");
        }

        Profile profile = profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        Set<Skill> skillsToAdd = new HashSet<>();

        // Add skills by ID
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            skillsToAdd.addAll(skillRepository.findByIdIn(request.getSkillIds()));
        }

        // Add skills by name (create if not exists)
        if (request.getSkillNames() != null && !request.getSkillNames().isEmpty()) {
            for (String name : request.getSkillNames()) {
                Skill skill = skillRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> skillRepository.save(
                                Skill.builder()
                                        .name(name)
                                        .category(Skill.SkillCategory.OTHER)
                                        .build()
                        ));
                skillsToAdd.add(skill);
            }
        }

        // Attach skills to profile
        for (Skill skill : skillsToAdd) {
            profile.addSkill(skill);
        }

        profileRepository.save(profile);

        log.info("Added {} skills to profile for user {}", skillsToAdd.size(), userId);

        return skillsToAdd.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void removeSkillFromProfile(Long userId, Long skillId) {
        Profile profile = profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", skillId));

        profile.removeSkill(skill);
        profileRepository.save(profile);

        log.info("Removed skill {} from profile for user {}", skillId, userId);
    }

    @Transactional(readOnly = true)
    public Set<SkillResponse> getProfileSkills(Long userId) {
        Profile profile = profileRepository.findByUserIdWithSkills(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        return profile.getSkills()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toSet());
    }

    // -------------------- MAPPER --------------------

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .createdAt(skill.getCreatedAt())
                .build();
    }
}
