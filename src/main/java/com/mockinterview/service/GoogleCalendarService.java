package com.mockinterview.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.mockinterview.dto.oauth.OAuthStatusResponse;
import com.mockinterview.dto.oauth.TestConnectionResponse;
import com.mockinterview.entity.GoogleCalendarToken;
import com.mockinterview.entity.User;
import com.mockinterview.exception.BadRequestException;
import com.mockinterview.exception.ResourceNotFoundException;
import com.mockinterview.exception.UnauthorizedException;
import com.mockinterview.repository.GoogleCalendarTokenRepository;
import com.mockinterview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private final GoogleCalendarTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String APPLICATION_NAME = "Mock Interview Platform";

    public String getAuthorizationUrl(Long userId) {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport,
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    SCOPES
            )
                    .setDataStoreFactory(new MemoryDataStoreFactory())
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();

            String state = generateState(userId);

            return flow.newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .setState(state)
                    .build();

        } catch (Exception e) {
            log.error("Error generating authorization URL for user {}", userId, e);
            throw new BadRequestException("Failed to generate authorization URL");
        }
    }

    @Transactional
    public void handleCallback(Long userId, String code) {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport,
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    code,
                    redirectUri
            ).execute();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            // Get user's email from token
            String email = getUserEmailFromToken(tokenResponse.getAccessToken());

            // Delete existing token if any
            tokenRepository.deleteByUserId(userId);

            // Save new token
            GoogleCalendarToken token = GoogleCalendarToken.builder()
                    .user(user)
                    .accessToken(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresAt(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()))
                    .scope(String.join(",", SCOPES))
                    .connectedEmail(email)
                    .revoked(false)
                    .build();

            tokenRepository.save(token);

            // Update user status
            user.setGoogleCalendarConnected(true);
            user.setCalendarSyncEnabled(true);
            userRepository.save(user);

            log.info("Google Calendar connected successfully for user {}", userId);

        } catch (Exception e) {
            log.error("Error handling OAuth callback for user {}", userId, e);
            throw new BadRequestException("Failed to connect Google Calendar: " + e.getMessage());
        }
    }

    @Transactional
    public void disconnect(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Delete token
        tokenRepository.deleteByUserId(userId);

        // Update user status
        user.setGoogleCalendarConnected(false);
        user.setCalendarSyncEnabled(false);
        userRepository.save(user);

        log.info("Google Calendar disconnected for user {}", userId);
    }

    @Transactional(readOnly = true)
    public OAuthStatusResponse getOAuthStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        GoogleCalendarToken token = tokenRepository.findByUserIdAndRevokedFalse(userId)
                .orElse(null);

        OAuthStatusResponse.GoogleCalendarStatus calendarStatus;

        if (token != null && user.getGoogleCalendarConnected()) {
            calendarStatus = OAuthStatusResponse.GoogleCalendarStatus.builder()
                    .connected(true)
                    .connectedAt(token.getCreatedAt())
                    .email(token.getConnectedEmail())
                    .scope(Arrays.asList(token.getScope().split(",")))
                    .build();
        } else {
            calendarStatus = OAuthStatusResponse.GoogleCalendarStatus.builder()
                    .connected(false)
                    .connectedAt(null)
                    .email(null)
                    .scope(null)
                    .build();
        }

        return OAuthStatusResponse.builder()
                .googleCalendar(calendarStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public TestConnectionResponse testConnection(Long userId) {
        GoogleCalendarToken token = tokenRepository.findByUserIdAndRevokedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException("Google Calendar not connected"));

        try {
            Calendar service = getCalendarService(token);

            // Try to get primary calendar
            com.google.api.services.calendar.model.Calendar primaryCalendar =
                    service.calendars().get("primary").execute();

            return TestConnectionResponse.builder()
                    .connected(true)
                    .message("Connection is active")
                    .calendarName(primaryCalendar.getSummary())
                    .calendarEmail(primaryCalendar.getId())
                    .build();

        } catch (Exception e) {
            log.error("Error testing connection for user {}", userId, e);
            return TestConnectionResponse.builder()
                    .connected(false)
                    .message("Connection failed: " + e.getMessage())
                    .build();
        }
    }

    public String createCalendarEvent(
            Long userId,
            String summary,
            String description,
            ZonedDateTime startTime,
            ZonedDateTime endTime,
            String meetLink
    ) {
        GoogleCalendarToken token = tokenRepository.findByUserIdAndRevokedFalse(userId)
                .orElse(null);

        if (token == null) {
            log.warn("No Google Calendar token for user {}, skipping event creation", userId);
            return null;
        }

        try {
            Calendar service = getCalendarService(token);

            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description);

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startTime.toString()));
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endTime.toString()));
            event.setEnd(end);

            if (meetLink != null) {
                event.setLocation(meetLink);
            }

            Event createdEvent = service.events()
                    .insert("primary", event)
                    .execute();

            log.info("Calendar event created for user {}: {}", userId, createdEvent.getId());
            return createdEvent.getId();

        } catch (Exception e) {
            log.error("Error creating calendar event for user {}", userId, e);
            return null;
        }
    }

    public void updateCalendarEvent(
            Long userId,
            String eventId,
            ZonedDateTime newStartTime,
            ZonedDateTime newEndTime
    ) {
        GoogleCalendarToken token = tokenRepository.findByUserIdAndRevokedFalse(userId)
                .orElse(null);

        if (token == null || eventId == null) {
            return;
        }

        try {
            Calendar service = getCalendarService(token);

            Event event = service.events().get("primary", eventId).execute();

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(newStartTime.toString()));
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(newEndTime.toString()));
            event.setEnd(end);

            service.events().update("primary", eventId, event).execute();

            log.info("Calendar event updated for user {}: {}", userId, eventId);

        } catch (Exception e) {
            log.error("Error updating calendar event for user {}", userId, e);
        }
    }

    public void deleteCalendarEvent(Long userId, String eventId) {
        GoogleCalendarToken token = tokenRepository.findByUserIdAndRevokedFalse(userId)
                .orElse(null);

        if (token == null || eventId == null) {
            return;
        }

        try {
            Calendar service = getCalendarService(token);
            service.events().delete("primary", eventId).execute();

            log.info("Calendar event deleted for user {}: {}", userId, eventId);

        } catch (Exception e) {
            log.error("Error deleting calendar event for user {}", userId, e);
        }
    }

    private Calendar getCalendarService(GoogleCalendarToken token) throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Check if token is expired and refresh if needed
        if (token.isExpired() && token.getRefreshToken() != null) {
            refreshAccessToken(token);
        }

        Credential credential = new Credential.Builder(
                com.google.api.client.auth.oauth2.BearerToken.authorizationHeaderAccessMethod()
        )
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                .setClientAuthentication(
                        new com.google.api.client.auth.oauth2.ClientParametersAuthentication(
                                clientId, clientSecret
                        )
                )
                .build();

        credential.setAccessToken(token.getAccessToken());
        credential.setRefreshToken(token.getRefreshToken());

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Transactional
    protected void refreshAccessToken(GoogleCalendarToken token) throws IOException {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport,
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    token.getRefreshToken(),
                    redirectUri
            ).execute();

            token.setAccessToken(response.getAccessToken());
            token.setExpiresAt(Instant.now().plusSeconds(response.getExpiresInSeconds()));

            if (response.getRefreshToken() != null) {
                token.setRefreshToken(response.getRefreshToken());
            }

            tokenRepository.save(token);

            log.info("Access token refreshed for user {}", token.getUser().getId());

        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new IOException("Failed to refresh access token", e);
        }
    }

    private String getUserEmailFromToken(String accessToken) {
        // In production, you'd call Google's userinfo endpoint
        // For now, return null and it will be updated later
        return null;
    }

    private String generateState(Long userId) {
        return "user_" + userId + "_" + System.currentTimeMillis();
    }

    public Long extractUserIdFromState(String state) {
        if (state == null || !state.startsWith("user_")) {
            throw new BadRequestException("Invalid state parameter");
        }

        String[] parts = state.split("_");
        if (parts.length < 2) {
            throw new BadRequestException("Invalid state parameter");
        }

        return Long.parseLong(parts[1]);
    }

    public String getFrontendSuccessUrl() {
        return frontendUrl + "/settings/calendar?status=success";
    }

    public String getFrontendErrorUrl(String error) {
        return frontendUrl + "/settings/calendar?status=error&message=" + error;
    }
}