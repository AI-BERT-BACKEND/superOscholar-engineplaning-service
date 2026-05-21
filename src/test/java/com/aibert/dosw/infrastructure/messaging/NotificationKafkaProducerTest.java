package com.aibert.dosw.infrastructure.messaging;

import com.aibert.dosw.infrastructure.messaging.dto.NotificationEvent;
import com.aibert.dosw.infrastructure.messaging.dto.NotificationEventType;
import com.aibert.dosw.infrastructure.messaging.dto.NotificationSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private NotificationKafkaProducer producer;

    @BeforeEach
    void setUp() {
        producer = new NotificationKafkaProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "topic", "planning.notifications");
    }

    @Test
    void shouldSendNotificationSuccessfully() {
        NotificationEvent event = NotificationEvent.builder()
                .userId("user-123")
                .type(NotificationEventType.STUDY_SUGGESTION)
                .title("Study now")
                .message("You have critical tasks")
                .severity(NotificationSeverity.HIGH)
                .relatedEntityId("user-123")
                .build();

        producer.send(event);

        verify(kafkaTemplate).send(eq("planning.notifications"), eq("user-123"), eq(event));
    }

    @Test
    void shouldHandleExceptionGracefullyDuringSend() {
        NotificationEvent event = NotificationEvent.builder()
                .userId("user-456")
                .type(NotificationEventType.OVERLOAD_ALERT)
                .title("Alert")
                .message("Overloaded schedule")
                .severity(NotificationSeverity.MEDIUM)
                .relatedEntityId("user-456")
                .build();

        doThrow(new RuntimeException("Kafka connection refused"))
                .when(kafkaTemplate).send(any(), any(), any());

        // Should not throw — errors are swallowed and logged
        producer.send(event);

        verify(kafkaTemplate).send(eq("planning.notifications"), eq("user-456"), eq(event));
    }

    @Test
    void shouldHandleNullUserIdInEvent() {
        NotificationEvent event = NotificationEvent.builder()
                .userId(null)
                .type(NotificationEventType.STUDY_SUGGESTION)
                .title("Test")
                .message("Test message")
                .severity(NotificationSeverity.LOW)
                .build();

        producer.send(event);

        verify(kafkaTemplate).send(eq("planning.notifications"), eq(null), eq(event));
    }
}
