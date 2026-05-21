package com.aibert.dosw.infrastructure.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userId;
    private NotificationEventType type;
    private String title;
    private String message;
    private NotificationSeverity severity;
    private String relatedEntityId;
}
