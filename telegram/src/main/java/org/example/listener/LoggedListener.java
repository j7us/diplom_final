package org.example.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.message.LoggedMessage;
import org.example.service.UserStatusService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggedListener {
    private final UserStatusService userStatusService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "logged")
    public void listen(String message) {
        try {
            LoggedMessage loggedMessage = objectMapper.readValue(message, LoggedMessage.class);
            userStatusService.markLoggedIn(loggedMessage.getChatId());
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось обработать сообщение авторизации", exception);
        }
    }
}
