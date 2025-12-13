package org.example.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaTemplateConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    //Фабрика, которая создаёт продюсеров (отправителей)
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Адрес Kafka (берётся из application.properties или по умолчанию localhost:9092)
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Ключ сообщения будет сериализован как строка (email пользователя как ключ)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Значение сообщения (UserEvent) будет сериализовано в JSON
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Настройки для надёжности
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждём подтверждения от всех реплик
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);   // Количество повторных попыток

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    //KafkaTemplate - главный инструмент для отправки сообщений в Spring Kafka
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
