package com.mockinterview.repository;

import com.mockinterview.entity.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {

    Optional<GoogleCalendarToken> findByUserId(Long userId);

    Optional<GoogleCalendarToken> findByUserIdAndRevokedFalse(Long userId);

    boolean existsByUserId(Long userId);

    @Modifying
    @Query("UPDATE GoogleCalendarToken t SET t.revoked = true WHERE t.user.id = :userId")
    void revokeByUserId(Long userId);

    void deleteByUserId(Long userId);
}