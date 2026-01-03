package com.mockinterview.dto.oauth;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthStatusResponse {
    private GoogleCalendarStatus googleCalendar;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoogleCalendarStatus {
        private Boolean connected;
        private Instant connectedAt;
        private String email;
        private List<String> scope;
    }
}