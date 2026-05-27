package org.example.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.adapter.kafka.dto.TelegramLoginRequestMessage;
import org.example.adapter.kafka.dto.TelegramNameRequestMessage;
import org.example.application.service.TelegramIntegrationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramRequestListener {
    private final ObjectMapper objectMapper;
    private final TelegramIntegrationService telegramIntegrationService;

    @KafkaListener(topics = "login")
    public void listenLogin(String message) {
        try {
            TelegramLoginRequestMessage request = objectMapper.readValue(message, TelegramLoginRequestMessage.class);
            telegramIntegrationService.processLogin(request.getChatId(), request.getMessage());
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось обработать сообщение логина из Telegram", exception);
        }
    }

    @KafkaListener(topics = "enterprise_res")
    public void listenEnterpriseRequest(String message) {
        try {
            TelegramNameRequestMessage request = objectMapper.readValue(message, TelegramNameRequestMessage.class);
            telegramIntegrationService.processEnterpriseMileageRequest(request.getChatId(), request.getName());
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось обработать запрос предприятия из Telegram", exception);
        }
    }

    @KafkaListener(topics = "vehicle_res")
    public void listenVehicleRequest(String message) {
        try {
            TelegramNameRequestMessage request = objectMapper.readValue(message, TelegramNameRequestMessage.class);
            telegramIntegrationService.processVehicleMileageRequest(request.getChatId(), request.getName());
        } catch (Exception exception) {
            throw new RuntimeException("Не удалось обработать запрос машины из Telegram", exception);
        }
    }
}
