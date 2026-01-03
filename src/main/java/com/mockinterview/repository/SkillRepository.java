package com.mockinterview.repository;

import com.mockinterview.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Skill> findByCategory(Skill.SkillCategory category);

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Skill> searchByName(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Skill s WHERE s.category = :category AND LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Skill> searchByCategoryAndName(
            @Param("category") Skill.SkillCategory category,
            @Param("search") String search,
            Pageable pageable
    );

    Set<Skill> findByIdIn(Set<Long> ids);

    List<Skill> findByNameInIgnoreCase(List<String> names);
}