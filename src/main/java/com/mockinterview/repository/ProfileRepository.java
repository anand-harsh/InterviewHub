package com.mockinterview.repository;

import com.mockinterview.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills WHERE p.id = :id")
    Optional<Profile> findByIdWithSkills(Long id);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithSkills(Long userId);

    // New methods for matching

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.user.id <> :userId " +
            "AND (:availableOnly = false OR p.availableForInterview = true)")
    Page<Profile> findAllExcludingUserAndAvailable(
            @Param("userId") Long userId,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable
    );

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.user.id <> :userId")
    List<Profile> findAllExcludingUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.skills")
    List<Profile> findAllWithSkills();

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.availableForInterview = true AND p.user.id <> :userId")
    List<Profile> findAvailableProfilesExcludingUser(@Param("userId") Long userId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.targetRole = :targetRole AND p.user.id <> :userId " +
            "AND p.availableForInterview = true")
    List<Profile> findByTargetRoleExcludingUser(
            @Param("targetRole") String targetRole,
            @Param("userId") Long userId
    );

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.currentRole = :currentRole AND p.user.id <> :userId " +
            "AND p.availableForInterview = true")
    List<Profile> findByCurrentRoleExcludingUser(
            @Param("currentRole") String currentRole,
            @Param("userId") Long userId
    );

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.timezone = :timezone AND p.user.id <> :userId " +
            "AND p.availableForInterview = true")
    List<Profile> findByTimezoneExcludingUser(
            @Param("timezone") Profile.Timezone timezone,
            @Param("userId") Long userId
    );

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills " +
            "WHERE p.yearsOfExperience BETWEEN :minExp AND :maxExp " +
            "AND p.user.id <> :userId AND p.availableForInterview = true")
    List<Profile> findByExperienceRangeExcludingUser(
            @Param("minExp") Integer minExp,
            @Param("maxExp") Integer maxExp,
            @Param("userId") Long userId
    );
}