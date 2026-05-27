package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.message.LoginMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginRequestPublisher {
    private static final String LOGIN_TOPIC = "login";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Long chatId, String message) {
        LoginMessage loginMessage = new LoginMessage(chatId, message);

        try {
            kafkaTemplate.send(LOGIN_TOPIC, objectMapper.writeValueAsString(loginMessage));
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось отправить сообщение авторизации", exception);
        }
    }
}
