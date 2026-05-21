package com.aibert.dosw.infrastructure.messaging;

import com.aibert.dosw.infrastructure.messaging.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.notifications:planning.notifications}")
    private String topic;

    public NotificationKafkaProducer(
            @Qualifier("planningKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(NotificationEvent event) {
        try {
            kafkaTemplate.send(topic, event.getUserId(), event);
            log.info("Notification sent: type={}, userId={}, severity={}",
                    event.getType(), event.getUserId(), event.getSeverity());
        } catch (Exception e) {
            log.error("Failed to send notification: type={}, userId={}: {}",
                    event.getType(), event.getUserId(), e.getMessage(), e);
        }
    }
}
