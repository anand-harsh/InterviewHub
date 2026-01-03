package com.mockinterview.repository;

import com.mockinterview.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills WHERE p.id = :id")
    Optional<Profile> findByIdWithSkills(Long id);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.skills WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithSkills(Long userId);
}