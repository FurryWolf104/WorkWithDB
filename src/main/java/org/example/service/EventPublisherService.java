package org.example.service;

import org.example.event.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.user-events:user-events-topic}")
    private String userEventsTopic;

    /**
     * Отправляет событие о создании пользователя в Kafka.
     * @param email Email созданного пользователя
     */
    public void publishUserCreated(String email) {
        UserEvent event = new UserEvent(email, "CREATED");
        sendEvent(event, email);
    }

    /**
     * Отправляет событие об удалении пользователя в Kafka.
     * @param email Email удалённого пользователя
     */
    public void publishUserDeleted(String email) {
        UserEvent event = new UserEvent(email, "DELETED");
        sendEvent(event, email);
    }

    /**
     * Общий метод для отправки события в Kafka.
     * Ключом сообщения является email - это обеспечивает порядок обработки
     * событий одного пользователя.
     */
    private void sendEvent(UserEvent event, String key) {
        try {
            // Отправляем сообщение в топик Kafka
            kafkaTemplate.send(userEventsTopic, key, event);
            System.out.println("Отправлено событие в Kafka: " + event);
        } catch (Exception e) {
            // В реальном проекте здесь нужно логировать ошибку или использовать Dead Letter Queue
            System.err.println("Ошибка отправки события в Kafka: " + e.getMessage());
            throw new RuntimeException("Не удалось отправить событие в Kafka", e);
        }
    }
}
