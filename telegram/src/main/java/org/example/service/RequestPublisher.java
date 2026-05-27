package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.message.RequestMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, Long chatId, String name) {
        RequestMessage requestMessage = new RequestMessage(chatId, name);

        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(requestMessage));
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось отправить сообщение запроса", exception);
        }
    }
}
