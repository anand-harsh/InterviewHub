package com.mockinterview.dto.oauth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestConnectionResponse {
    private Boolean connected;
    private String message;
    private String calendarName;
    private String calendarEmail;
}